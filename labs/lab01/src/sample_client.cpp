#include <iostream>
#include <winsock2.h>

int main(int argc, char* argv[]) {
    //WSAStartup
    WSAData wsaData;
    WORD DLLVersion = MAKEWORD(2, 1);
    if(WSAStartup(DLLVersion, &wsaData) != 0) {
        std::cout << "Error" << std::endl;
        exit(1);
    }

    SOCKADDR_IN addr;
    int sizeofaddr = sizeof(addr);
    addr.sin_addr.s_addr = inet_addr("127.0.0.1");
    addr.sin_port = htons(1111);
    addr.sin_family = AF_INET;

    SOCKET Connection = socket(AF_INET, SOCK_STREAM, NULL);
    if(connect(Connection, (SOCKADDR*)&addr, sizeof(addr)) != 0) {
        std::cout << "Error: failed connect to server.\n";
        return 1;
    }
    std::cout << "Connected!" << std::endl << "Enter coords: ";
    char msg[256];
    std::cin.getline(msg,sizeof(msg));
    send(Connection, msg, sizeof(msg), NULL);
    char msg2[256];
    recv(Connection, msg2, sizeof(msg2), NULL);
    std::cout << msg2 << std::endl;
    return 0;
}