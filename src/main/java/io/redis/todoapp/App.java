package io.redis.todoapp;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

@SpringBootApplication
public class App {
    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void loadDotEnv() {
        try {
            Dotenv dotenv = Dotenv.configure().load();
            dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        } catch (DotenvException e) {
            logger.debug("no .env found, assuming environment variables are already set");

            System.getenv().forEach((k, v) -> System.setProperty(k, v));
        }
    }
    
    public static void main(String[] args) {
        loadDotEnv();

        SpringApplication.run(App.class, args);
    }

}
