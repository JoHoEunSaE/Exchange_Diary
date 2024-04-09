package org.johoeunsae.exchangediary.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.johoeunsae.exchangediary.ExchangediaryApplication;

public interface EnvironmentPathUtil {

	Logger log = LogManager.getLogger(EnvironmentPathUtil.class);

	static InputStream toStream(String input) throws IOException {
		String currentPath = null;
		URL resource = ExchangediaryApplication.class.getClassLoader().getResource(input);
		if (Objects.isNull(resource)) {
			currentPath = ExchangediaryApplication.class.getClassLoader()
					.getResource("classpath:" + input).toString();
		} else {
			currentPath = resource.toString();
		}
		if (currentPath == null) {
			log.error("Path를 찾을 수 없습니다. baseUrl: " + ExchangediaryApplication.class.getClassLoader()
					.getResource(""));
			throw new IOException("Path를 찾을 수 없습니다.");
		}
		return ExchangediaryApplication.class.getClassLoader().getResourceAsStream(input);
	}
}
