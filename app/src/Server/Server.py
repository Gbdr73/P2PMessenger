import socket
import traceback
import os
import time
from datetime import datetime
from database import Base

########################
# config begin
########################

ADDRESS_IN = "192.168.1.3"
PORT_IN = 5080

# timeout in seconds
TIMEOUT_RECV = 100
TIMEOUT_SEND = 30
TIMEOUT_CONNECT = 30

RECV_BUFFER_SIZE = 4096

########################
# Request types
########################
REQ_REGISTRATION = b'reg'
REQ_LOGIN = b'lgn'
REQ_PING = b'png'
REQ_GET_IP = b'gip'

########################
# Responce codes
########################
CODE_REQ_OK = b'000'
CODE_REQ_ERR = b'001'
CODE_NAME_EXIST = b'002'
CODE_LOGIN_ERROR = b'003'
CODE_NAME_ABSENT = b'004'

########################
# inner constants
########################

DEFAULT_LOGGING_FILENAME = os.path.join("logs", f"{datetime.now().date()}.txt")
NAME_COLUMN = "name"
HASH_COLUMN = "hash"
IP_COLUMN = "ip"

########################
# session constants
########################

logging_filename = DEFAULT_LOGGING_FILENAME

address_out = None
port_out = None

########################

def log(data = ""):
    print(data)
    with open(logging_filename, "a", encoding="utf-8") as file:
        file.write(data)
        file.write("\n")

def print_bytes(data : bytes):
    """Prints bytes as a block"""

    lines = []

    NIBBLES = 2
    NIBBLES_IN_LINE = 32
    NIBBLES_SEPARATED = 16

    i = 0
    line = ""
    for nibble in data.hex():
        line += nibble
        i += 1
        if i % NIBBLES == 0:
            line += " "
        if i % NIBBLES_IN_LINE == 0:
            lines.append(line)
            line = ""
            continue
        if i % NIBBLES_SEPARATED == 0:
            line += "| "
    
    if i % NIBBLES_IN_LINE != 0:
        lines.append(line)

    return lines
    
def print_received_data(data : bytes, address, separator : str):
    log(f"{datetime.now()}: Received {len(data)} bytes from {address}:")
    log(f"{'':{separator}<50}")

    lines = print_bytes(data)
    for line in lines:
        log(line)
    log("")


def receive_data(connection : socket.socket, identificator):
    data = bytes()

    connection.settimeout(TIMEOUT_RECV)
    data = connection.recv(RECV_BUFFER_SIZE)
    if not data:
        log(f"{datetime.now()}: Connection forcefully closed by {identificator}")

    return data

def do_register(data_in : bytes, address : str) -> bytes:
    data_out = CODE_REQ_ERR

    str_in = data_in.decode('utf-8')
    str_data = str_in[:2]

    if str_data.isdecimal():
        index = int(str_data) + 2

        if index < len(str_in):
            str_name = str_in[2:index]
            str_hash = str_in[index:]
            print(str_hash)

            if len(str_hash) == 64:
                find_data = {NAME_COLUMN:str_name}
                data_base = Base()
                find_res = data_base.find(find_data)

                if find_res.empty:
                    dict = {NAME_COLUMN:str_name, HASH_COLUMN:str_hash, IP_COLUMN:address}
                    data_base.append(dict)
                    data_out = CODE_REQ_OK
                else:
                    data_out = CODE_NAME_EXIST

    return data_out

def do_login(data_in : bytes, address : str) -> bytes:
    data_out = CODE_REQ_ERR

    str_in = data_in.decode('utf-8')
    str_data = str_in[:2]

    if str_data.isdecimal():
        index = int(str_data) + 2

        if index < len(str_in):
            str_name = str_in[2:index]
            str_hash = str_in[index:]
            print(str_hash)

            if len(str_hash) == 64:
                find_data = {NAME_COLUMN:str_name, HASH_COLUMN:str_hash}
                data_base = Base()
                find_res = data_base.find(find_data)

                if find_res.empty:
                    data_out = CODE_LOGIN_ERROR
                else:
                    new_ip = {IP_COLUMN:address}
                    data_base.modify(find_res.index, new_ip)
                    data_out = CODE_REQ_OK

    return data_out

def do_get_ip(data_in : bytes) -> bytes:
    data_out = CODE_REQ_ERR

    str_in = data_in.decode('utf-8')
    str_data = str_in[:2]

    if str_data.isdecimal():
        index = int(str_data) + 2

        if index < len(str_in):
            str_name = str_in[2:index]
            str_hash = str_in[index:index + 64]
            index = index + 64
            str_data = str_in[index:index + 2]
            index = index + 2

            log("str_name = " + str_name)
            log("str_hash = " + str_hash)

            if len(str_hash) == 64 and len(str_data) == 2 and str_data.isdecimal():
                end = index + int(str_data)
                str_user = str_in[index:end]

                log("str_user = " + str_user)

                if len(str_user) > 0:
                    find_data = {NAME_COLUMN:str_name, HASH_COLUMN:str_hash}
                    data_base = Base()
                    find_res = data_base.find(find_data)

                    if find_res.empty:
                        log("Login error")
                        data_out = CODE_LOGIN_ERROR
                    else:
                        find_data = {NAME_COLUMN:str_user}
                        find_res = data_base.find(find_data)

                        if find_res.empty:
                            log("User not found")
                            data_out = CODE_NAME_ABSENT
                        else:
                            address = find_res.at[1, IP_COLUMN]
                            data_out = CODE_REQ_OK + address.encode('utf-8')

    return data_out

def prepare_answer(data_in : bytes, address : str) -> bytes:
    data_out = None

    addr = str(address)
    index = addr.find("'") + 1
    addr = addr[index:]
    index = addr.find("'")
    addr = addr[:index]

    index = data_in.find(REQ_REGISTRATION)

    if index == 0:
        print("Registration request")
        data_out = do_register(data_in[3:], addr)
    else:
        index = data_in.find(REQ_LOGIN)

        if index == 0:
            print("Login request")
            data_out = do_login(data_in[3:], addr)
        else:
            index = data_in.find(REQ_PING)

            if index == 0:
                print("Ping request")
                data_out = do_login(data_in[3:], addr)
            else:
                index = data_in.find(REQ_GET_IP)

                if index == 0:
                    print("Get IP request")
                    data_out = do_get_ip(data_in[3:])
                else:
                    print("Unkwown request")
                    data_out = CODE_REQ_ERR

    if data_out is not None:
        print_received_data(data_out, ADDRESS_IN, "<")

    return data_out

def send_answer(connection : socket.socket, data_out : bytes):
    if data_out is None: return
    if len(data_out) == 0: return

    connection.settimeout(TIMEOUT_SEND)
    connection.send(data_out)

def serve_connection(conn_in : socket.socket, client_address):
    log(f"{datetime.now()}: Connected by {client_address}")

    conn_in.settimeout(TIMEOUT_RECV)
    data_in = receive_data(conn_in, client_address)
    if len(data_in) == 0:
        log(f"No data received from {client_address}")
        return
    print_received_data(data_in, client_address, ">")

    data_out = prepare_answer(data_in, client_address)
    send_answer(conn_in, data_out)

def server_cycle(server_socket : socket.socket):
    while True:
        log("\nWaiting for connection...")
        
        try:         
            conn_in, client_address = server_socket.accept()       
            with conn_in:
                serve_connection(conn_in, client_address)
        except TimeoutError:
            log(f"{datetime.now()} Timout error: {ADDRESS_IN}:{PORT_IN}")
        except Exception as exc:
            traceback.print_exception(type(exc), exc, exc.__traceback__)
            log("Error:" + str(exc.args))

########################
# setup
########################

def fatal_error(message = None):
    if message is not None:
        print(message)
    input("Press enter to exit...")
    exit(-1)

def init_log_file():
    global logging_filename
    target_directory = os.path.join("logs")
    logging_filename = os.path.join(target_directory, f"{datetime.now().date()}.txt")
    if not os.path.exists(target_directory): os.makedirs(target_directory)
    print("log file:", os.path.abspath(logging_filename))

def init():
    init_log_file()


def main():
    init()

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
        server_socket.bind((ADDRESS_IN, PORT_IN))
        server_socket.listen(4)
        log(f"Listen socket: {ADDRESS_IN}:{PORT_IN}")

        server_cycle(server_socket)

if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print()
        traceback.print_exception(type(exc), exc, exc.__traceback__)
        log("Error:" + str(exc.args))
        fatal_error()