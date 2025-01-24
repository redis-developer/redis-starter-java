package io.redis.todoapp.util;

import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
public class GsonConfiguration {
    @Bean
    public Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new GsonInstantTypeAdapter())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
                .create();
    }
}
