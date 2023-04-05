cmake_minimum_required(VERSION 3.4.1)

add_library(serial_port
            SHARED
        serialport.cpp)

find_library(log-lib
             log )

target_link_libraries(serial_port
                      ${log-lib} )

add_library(rtmp
        SHARED
        rtmp.cpp)

find_library(log-lib
        log )

target_link_libraries(rtmp
        ${log-lib} )