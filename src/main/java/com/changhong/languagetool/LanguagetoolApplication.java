package com.changhong.languagetool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.changhong.languagetool.mapper")
public class LanguagetoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(LanguagetoolApplication.class, args);
    }

}

