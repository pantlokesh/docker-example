package com.linkedin.docker.example;

import com.linkedin.docker.example.entity.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


@Configuration
class AppConfig {

  @Bean
  public JedisConnectionFactory redisConnectionFactory() {
    final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("docker-example-redis", 6379);
    return new JedisConnectionFactory(config);
  }

  @Bean
  public RedisTemplate<Integer, Product> redisTemplate(){
    RedisTemplate<Integer, Product> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }
}
