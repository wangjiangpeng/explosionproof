//
// Created by wangjp on 2023/3/31.
//

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

void test(){
    int fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

}