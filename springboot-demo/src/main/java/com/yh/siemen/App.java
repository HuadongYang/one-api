package com.yh.siemen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan("com.yh.siemen.**.mapper")
public class App 
{
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println(System.getProperty("user.dir"));
    }
}
