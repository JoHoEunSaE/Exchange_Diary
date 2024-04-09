package org.johoeunsae.exchangediary.cache.config;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "cache.caffeine")
public class CaffeineCacheProperties {

	private final String type;

	private final Map<String, Cache> caches;

	public CaffeineCacheProperties(String type, List<Cache> caches) {
		this.type = type;
		this.caches = caches.stream().collect(
				java.util.stream.Collectors.toMap(Cache::getName, cache -> cache));
	}

	@Getter
	public static class Cache {

		private final String name;
		private final Long expireAfterWrite;
		private final Long maximumSize;

		public Cache(String name, Long expireAfterWrite, Long maximumSize) {
			this.name = name;
			this.expireAfterWrite = expireAfterWrite;
			this.maximumSize = maximumSize;
		}
	}
}
