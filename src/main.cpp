#include <iostream>
#include <winsock2.h>
#include <SFML/Graphics.hpp>

int main() {
    sf::RenderWindow window(sf::VideoMode(600, 600), "Waiting for connection...");
    sf::RectangleShape shape;
    sf::Vector2f size;
    sf::Vector2f position;

    while (window.isOpen()) {
        sf::Event event{};
        while (window.pollEvent(event)) {
            if (event.type == sf::Event::Closed)
                window.close();
        }

        WSAData wsaData;
        WORD DLLVersion = MAKEWORD(2, 1);
        if (WSAStartup(DLLVersion, &wsaData) != 0) {
            std::cout << "Error" << std::endl;
            exit(1);
        }

        SOCKADDR_IN addr;
        int sizeofaddr = sizeof(addr);
        addr.sin_addr.s_addr = inet_addr("127.0.0.1");
        addr.sin_port = htons(1111);
        addr.sin_family = AF_INET;

        SOCKET sListen = socket(AF_INET, SOCK_STREAM, 0);
        bind(sListen, (SOCKADDR *) &addr, sizeof(addr));
        listen(sListen, SOMAXCONN);

        SOCKET newConnection;
        newConnection = accept(sListen, (SOCKADDR *) &addr, &sizeofaddr);

        if (newConnection == 0) {
            std::cout << "Error #2\n";
        } else {
            window.setTitle("Client Connected!");
            char msg[256];
            recv(newConnection, msg, sizeof(msg), 0);
            float receivedDigits[4];
            char *token = strtok(msg, ";");
            uint8_t iterator = 0;
            receivedDigits[iterator] = strtof(token, nullptr);
            ++iterator;
            while (iterator < sizeof(receivedDigits) / sizeof(receivedDigits[0]) && token != NULL) {
                token = strtok(nullptr, ";");
                if (token != nullptr)
                    receivedDigits[iterator] = strtof(token, nullptr);
                ++iterator;
            }

            std::cout << receivedDigits[0] << receivedDigits[1] << receivedDigits[2] << receivedDigits[3] << std::endl;
            send(newConnection, "OK", sizeof(msg), 0);

            size.x = receivedDigits[0];
            size.y = receivedDigits[1];
            position.x = receivedDigits[2];
            position.y = receivedDigits[3];

            shape.setFillColor(sf::Color::Green);
            shape.setSize(size);
            shape.setPosition(position);
        }

        window.clear();
        window.draw(shape);
        window.display();
    }
    WSACleanup();
    return 0;
}