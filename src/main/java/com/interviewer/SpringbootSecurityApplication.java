package com.interviewer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.interviewer")
@MapperScan("com.interviewer.mapper")
@EnableAsync
public class SpringbootSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootSecurityApplication.class, args);
        System.out.println("项目启动成功");
    }

}
