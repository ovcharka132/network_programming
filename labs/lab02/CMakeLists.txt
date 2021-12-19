project(image_manipulation_multi_thread)

include_directories(include)
include_directories(include C:/SFML/include)

file(GLOB SRC src/*.cpp src/*.cxx)
add_executable(${PROJECT_NAME} ${SRC})

set(SFML_ROOT C:/SFML/)
set(SFML_DIR "${SFML_ROOT}/lib/cmake/SFML")
find_package(SFML 2.5 REQUIRED COMPONENTS graphics)

target_link_libraries(${PROJECT_NAME} sfml-graphics ${COMMON_DEPENDENCIES})