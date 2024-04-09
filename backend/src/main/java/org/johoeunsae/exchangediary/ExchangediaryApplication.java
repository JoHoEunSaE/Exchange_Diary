package org.johoeunsae.exchangediary;

import org.johoeunsae.exchangediary.cache.config.CaffeineCacheProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({CaffeineCacheProperties.class})
public class ExchangediaryApplication {

	public static void main(String[] args) {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
		SpringApplication.run(ExchangediaryApplication.class, args);
	}

}
