package com.community.front;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 前台APP接口启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.community"})
@MapperScan({"com.community.front.mapper", "com.community.common.mapper"})
public class CommunityFrontApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityFrontApplication.class, args);
    }
}
