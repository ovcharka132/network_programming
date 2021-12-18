project(image_manipulation)

add_executable(${PROJECT_NAME} src/main.cpp)
add_executable("${PROJECT_NAME}_client" src/sample_client.cpp)

include_directories(include C:/SFML/include)

set(SFML_ROOT C:/SFML/)
set(SFML_DIR "${SFML_ROOT}/lib/cmake/SFML")
find_package(SFML 2.5 REQUIRED COMPONENTS graphics)

target_link_libraries(${PROJECT_NAME} sfml-graphics wsock32 ws2_32)
target_link_libraries(${PROJECT_NAME}_client wsock32 ws2_32)