package org.johoeunsae.exchangediary.cache.domain;

public enum CacheManagerType {
	CAFFEINE("Caffeine");

	private final String type;

	CacheManagerType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
