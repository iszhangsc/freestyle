package com.freestyle.core.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

import static java.util.Collections.singletonMap;

/**
 * 自定义Redis配置,并且开启缓存支持
 *
 * @author zhangshichang
 * @date 2019/8/27 下午2:12
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     *  自定义的缓存key的生成策略 若想使用这个key
     *              只需要讲注解上keyGenerator的值设置为keyGenerator即可</br>
     * @return 自定义策略生成的key
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (Object target, Method method, Object... params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getDeclaringClass().getName());
            Arrays.stream(params).map(Object::toString).forEach(sb::append);
            return sb.toString();
        };
    }


    /**
     * RedisTemplate 配置
     * @return  RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        // 序列化配置
        final Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();

        // 配置RedisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        // key 序列化
        redisTemplate.setKeySerializer(stringSerializer);
        // value 序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // Hash key 序列化
        redisTemplate.setHashKeySerializer(stringSerializer);
        // Hash value 序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 缓存配置管理器
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        // 配置序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1));
        RedisCacheConfiguration redisCacheConfiguration = config.serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));


        // 以锁写入的方式创建RedisCacheWriter对象
        //RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(factory);
        // 创建默认缓存配置对象
        /* 默认配置，设置缓存有效期 1小时*/
        //RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1));
        /* 配置test的超时时间为120s*/
        return RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter(lettuceConnectionFactory)).cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(singletonMap("test", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(120)).disableCachingNullValues()))
                .transactionAware().build();

    }

}
