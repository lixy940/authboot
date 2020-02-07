package com.lixy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@MapperScan("com.lixy.mapper")
@SpringBootApplication
public class AuthBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthBootApplication.class, args);
    }

}
