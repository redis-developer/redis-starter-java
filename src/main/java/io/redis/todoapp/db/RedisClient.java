package io.redis.todoapp.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.UnifiedJedis;

@Configuration
public class RedisClient {
    private static UnifiedJedis jedis;
    
    public RedisClient() {
        if (jedis == null) {
            jedis = new UnifiedJedis(System.getProperty("REDIS_URL"));
        }
    }

    @Bean
    public UnifiedJedis getClient() {
        return jedis;
    }

    public void destroyInstance() {
        if (jedis != null) {
            jedis.close();
        }

        jedis = null;
    }
}
