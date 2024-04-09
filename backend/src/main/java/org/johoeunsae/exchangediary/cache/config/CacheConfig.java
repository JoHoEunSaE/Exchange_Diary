package org.johoeunsae.exchangediary.cache.config;

import com.github.benmanes.caffeine.cache.Ticker;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.johoeunsae.exchangediary.cache.config.CaffeineCacheProperties.Cache;
import org.johoeunsae.exchangediary.cache.domain.CacheManagerType;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	private static final Long DEFAULT_EXPIRE_AFTER_WRITE = 60L;
	private static final Long DEFAULT_MAXIMUM_SIZE = 1000L;

	@Bean
	public CacheManager cacheManager(CaffeineCacheProperties caffeineCacheProperties,
			Ticker ticker) {
		Map<String, Cache> caches = caffeineCacheProperties.getCaches();
		if (caches == null || caches.isEmpty()) {
			return new NoOpCacheManager();
		}
		if (CacheManagerType.CAFFEINE.getType().equals(caffeineCacheProperties.getType())) {
//			@formatter:off
			List<CaffeineCache> caffeineCaches = caches.values().stream().map(cache ->
				new CaffeineCache(
					cache.getName(),
					Caffeine.newBuilder()
					.expireAfterWrite(
						Optional.ofNullable(cache.getExpireAfterWrite()).orElse(DEFAULT_EXPIRE_AFTER_WRITE),
						java.util.concurrent.TimeUnit.SECONDS
					)
					.maximumSize(Optional.ofNullable(cache.getMaximumSize()).orElse(DEFAULT_MAXIMUM_SIZE))
					.ticker(ticker)
					.build())
				)
				.collect(Collectors.toList());
//          @formatter:on
			SimpleCacheManager manager = new SimpleCacheManager();
			manager.setCaches(caffeineCaches);
			return manager;
		}
		return new NoOpCacheManager();
	}

	@Bean
	public Ticker ticker() {
		return Ticker.systemTicker();
	}
}
