package com.community.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 物业后台管理启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.community"})
@MapperScan({"com.community.backend.mapper", "com.community.common.mapper"})
public class CommunityBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityBackendApplication.class, args);
    }
}
