package org.example.imagedownloader.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class ResilientCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis cache GET failed for cache '{}', key '{}'. Falling back to API. Error: {}", 
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Redis cache PUT failed for cache '{}', key '{}'. Error: {}", 
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis cache EVICT failed for cache '{}', key '{}'. Error: {}", 
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Redis cache CLEAR failed for cache '{}'. Error: {}", 
                cache.getName(), exception.getMessage());
    }
}
