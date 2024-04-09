package org.johoeunsae.exchangediary.utils.obfuscation;

import java.lang.reflect.Field;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.johoeunsae.exchangediary.exception.status.UtilsExceptionStatus;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class ContentEncodeAspect {

	private final DataEncoder dataEncoder;

	/**
	 * @param dataEncode 난독화가 필요한 객체에 달아놓은 DataEncode 어노테이션
	 * @Around 로 돌려주기 위한 PointCut 메소드입니다.
	 */
	@Pointcut("@annotation(dataEncode)")
	public void dataEncodePointcut(DataEncode dataEncode) {
	}

	/**
	 * 난독화가 필요한 Class의 필드를 난독화합니다. TargetMapping 에 class - 난독화할 메소드 리스트
	 *
	 * @param joinPoint  AOP 를 통해 난독화가 필요한 메소드를 인터셉트한 JoinPoint
	 * @param dataEncode 난독화가 필요한 객체에 달아놓은 DataEncode 어노테이션
	 * @return 메소드에 전달된 인자를 난독화한 후, 메소드를 실행한 결과를 반환합니다.
	 * @throws Throwable 매핑된 필드가 없을 경우 UtilsExceptionStatus.NON_MAPPED_TARGET 예외를 던집니다.
	 */
	@Around(value = "dataEncodePointcut(dataEncode)", argNames = "joinPoint,dataEncode")
	public Object encode(ProceedingJoinPoint joinPoint, DataEncode dataEncode) throws Throwable {
		TargetMapping[] targetMappings = dataEncode.value();

		Object[] methodArgs = joinPoint.getArgs();
		boolean isMatchedAtLeastOnce = false;
		for (TargetMapping targetMapping : targetMappings) {
			Class<?> targetClass = targetMapping.clazz();
			String[] targetFields = targetMapping.fields();

			for (Object arg : methodArgs) {
				if (arg.getClass().equals(targetClass)) {
					isMatchedAtLeastOnce = true;
					for (String targetField : targetFields) {
						String toEncode = getFieldFromTarget(arg, targetField, String.class);
						setFieldToTarget(arg, targetField, dataEncoder.encode(toEncode));
					}
				}
			}
		}
		if (!isMatchedAtLeastOnce) {
			throw UtilsExceptionStatus.NON_MAPPED_TARGET.toControllerException();
		}
		return joinPoint.proceed(methodArgs);
	}

	/**
	 * 전달받은 target의 fieldName 에 해당하는 필드를 toConvert 타입으로 반환합니다.
	 *
	 * @param target    난독화가 필요한 필드를 가진 객체
	 * @param fieldName 난독화가 필요한 필드의 이름
	 * @param toConvert 난독화가 필요한 필드의 타입
	 * @param <T>       난독화가 필요한 필드의 타입
	 * @return 난독화가 필요한 필드를 난독화한 후, toConvert 타입으로 캐스팅하여 반환합니다.
	 */
	private <T> T getFieldFromTarget(Object target, String fieldName, Class<T> toConvert) {
		try {
			Field declaredField = target.getClass().getDeclaredField(fieldName);
			declaredField.setAccessible(true);
			Object targetField = declaredField.get(target);
			return toConvert.cast(targetField);
		} catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
			throw UtilsExceptionStatus.NON_MAPPED_FIELD.toControllerException();
		}
	}

	/**
	 * 난독화된 필드를 객체에 적용합니다. setAccessible(true) 를 통해 private 필드에 접근한 후 set을 통해 난독화합니다.
	 *
	 * @param target    난독화가 필요한 필드를 가진 객체
	 * @param fieldName 난독화가 필요한 필드의 이름
	 * @param value     난독화가 필요한 필드의 타입
	 */
	private void setFieldToTarget(Object target, String fieldName, Object value) {
		try {
			Field declaredField = target.getClass().getDeclaredField(fieldName);
			declaredField.setAccessible(true);
			declaredField.set(target, value);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw UtilsExceptionStatus.NON_MAPPED_FIELD.toControllerException();
		}
	}
}
