package io.redis.todoapp;

import java.time.Instant;

import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.redis.todoapp.util.Gson_InstantTypeAdapter;

@Configuration
public class GsonConfiguration {
    @Primary
    @Bean
    public GsonBuilderCustomizer typeAdapterRegistration() {
        return builder -> {
            builder
                .registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        };
    }
}