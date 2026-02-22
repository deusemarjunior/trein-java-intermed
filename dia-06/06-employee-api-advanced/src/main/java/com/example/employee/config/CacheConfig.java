package com.example.employee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * TODO 8: Configuração do Redis Cache.
 *
 * Esta classe já está pronta — define serialização JSON e TTL de 10 minutos.
 * Para habilitar o cache, você precisa:
 *
 * 1. Descomentar @EnableCaching na classe EmployeeApiAdvancedApplication
 * 2. Mudar spring.cache.type de "none" para "redis" no application.yml
 * 3. Adicionar @Cacheable no DepartmentService.findById()
 * 4. Adicionar @CacheEvict no DepartmentService.delete()
 */
@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );
    }
}
