package com.nott.FastSync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Nott
 * @Date 2023/6/27
 */

@SpringBootApplication
@ComponentScan("com.nott.FastSync.*")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
