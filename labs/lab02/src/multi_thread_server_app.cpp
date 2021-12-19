#include "multi_thread_server.h"

void exit_handler();

sf::Mutex globalMutex;
float receivedDigits[4] = {50.0f,75.0f,10.0f,10.0f};
SOCKET server_socket;
std::vector<THREAD_HANDLE> connection_pool;

int main(int argc, char* argv[])
{
    COMMAND_OPTIONS cmd_opts{"", DEFAULT_PORT};

    parse_cmd(argc, argv, &cmd_opts);

    atexit(exit_handler);
    atexit(common_exit_handler);
    common_init_handler();

    CHECK_IO((server_socket = create_tcp_socket()) > 0, -1, "Can't create socket\n");

    sockaddr_in server_addr;
    init_inet_address(&server_addr, cmd_opts.host, cmd_opts.port);

	//Bind socket to the address on the server
	CHECK_IO(!bind(server_socket, (sockaddr*)&server_addr, sizeof(sockaddr)), -1, "Can't bind socket to the port %d\n", cmd_opts.port);
	//Set socket as passive
	CHECK_IO(!listen(server_socket, CONNECTION_QUEUE), -1, "Error listening socket\n");

	printf("Server running at the port %d\n", cmd_opts.port);

    sf::RenderWindow window(sf::VideoMode(600, 600), "Image Manipulation");
    sf::RectangleShape shape;
    sf::Vector2f size;
    sf::Vector2f position;

	while (window.isOpen())
	{
        sf::Event event{};
        while (window.pollEvent(event)) {
            if (event.type == sf::Event::Closed) {
                window.close();
            }
        }

		struct sockaddr_in incom_addr;
		memset(&incom_addr, 0, sizeof(incom_addr));
		socklen_t len = sizeof(incom_addr);
		SOCKET socket;
		CHECK_IO((socket = accept(server_socket, (sockaddr*)&incom_addr, &len)) > 0, -1, "Can't accept connection\n");

        sf::Thread thread(&handle_connection, &socket);
        thread.launch();
        globalMutex.lock();
        size.x = receivedDigits[0];
        size.y = receivedDigits[1];
        position.x = receivedDigits[2];
        position.y = receivedDigits[3];
        globalMutex.unlock();

        shape.setFillColor(sf::Color::Green);
        shape.setSize(size);
        shape.setPosition(position);

        window.clear();
        window.draw(shape);
        window.display();

    }

	close_socket(server_socket);

	return 0;
}

void exit_handler()
{
    close_socket(server_socket);
}

THREAD_RESULT handle_connection(void* data) {
    SOCKET socket;
    CHECK_IO((socket = *(SOCKET*)data) > 0, (THREAD_RESULT)-1, "Invalid socket\n");
    struct sockaddr_in addr;
    socklen_t addr_len = sizeof(addr);
    CHECK_IO(!getpeername(socket, (sockaddr*)&addr, &addr_len), (THREAD_RESULT)-1, "Error retrieving peer info\n");
    char* str_in_addr = inet_ntoa(addr.sin_addr);
    printf("[%s:%d]>>%s\n", str_in_addr, ntohs(addr.sin_port), "Establish new connection");
    while (true) {
        char buffer[256] = "";
        int rc = recv(socket, buffer, sizeof(buffer), 0);
        if (rc > 0) {
            printf("[%s:%d]:%s\n", str_in_addr, ntohs(addr.sin_port), buffer);
            globalMutex.lock();
            char *token = strtok(buffer, " ");
            uint8_t iterator = 0;
            receivedDigits[iterator] = strtof(token, nullptr);
            ++iterator;
            while (iterator < sizeof(receivedDigits) / sizeof(receivedDigits[0]) && token != NULL) {
                token = strtok(nullptr, " ");
                if (token != nullptr)
                    receivedDigits[iterator] = strtof(token, nullptr);
                ++iterator;
            }
            globalMutex.unlock();
        }
        else {
            break;
        }
    }
    close_socket(socket);
    printf("[%s:%d]>>%s\n", str_in_addr,ntohs(addr.sin_port), "Close incoming connection");
    return 0;
}