package org.johoeunsae.exchangediary.exception.status;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ValidationExceptionStatusResolver {
	private final Map<String, ExceptionStatus> exceptionStatusMap;
	private final String packageName;

	public ValidationExceptionStatusResolver() {
		packageName = this.getClass().getPackageName();
		String basesPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		String packagePath = basesPath + packageName.replaceAll("[.]", "/");
		log.info("package path: {}", packagePath);
		File dir = new File(packagePath);
		File[] files = dir.listFiles();
		if (Objects.isNull(files)) {
			exceptionStatusMap = Map.of();
			return;
		}
		exceptionStatusMap = Arrays.stream(files)
				.filter(file -> file.getName().endsWith(".class"))
				.map(this::getExceptionStatusClass)
				.filter(Objects::nonNull)
				.flatMap(cls -> Arrays.stream(cls.getEnumConstants()))
				.collect(Collectors.toMap(e -> e.getErrorReason().getCode(), e -> e));
		log.debug("load validation exception enum: {}", exceptionStatusMap);
	}

	@SuppressWarnings("unchecked")
	private Class<? extends ExceptionStatus> getExceptionStatusClass(File classFile) {
		try {
			Class<?> cls = Class.forName(packageName + "."
					+ classFile.getName().substring(0, classFile.getName().lastIndexOf('.')));
			if (!cls.isInterface() && ExceptionStatus.class.isAssignableFrom(cls)) {
				return (Class<? extends ExceptionStatus>) cls;
			}
		} catch (ClassNotFoundException e) {
			return null;
		}
		return null;
	}
	public ExceptionStatus findByErrorCode(String code) {
		return Objects.requireNonNull(exceptionStatusMap.get(code), () -> "Invalid error code: " + code);
	}
}
