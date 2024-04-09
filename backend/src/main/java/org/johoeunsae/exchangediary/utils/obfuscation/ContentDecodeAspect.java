package org.johoeunsae.exchangediary.utils.obfuscation;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * base64로 인코딩된 데이터를 디코딩하는 AOP
 */

/**
 * @deprecated depth 가 1이상인 객체 (List<List<DecodeObject>) 같은경우, 안에 있는 객체 필드를 디코딩하는 기능을 수행이 불가능하므로,
 * @JsonSerialize(using = DecodeSerializer.class) 를 사용하여 직접 디코딩하도록 변경하였습니다.
 */
@Component
//@Aspect
@RequiredArgsConstructor
@Deprecated
public class ContentDecodeAspect {

	private final DataDecoder DataDecoder;

	/**
	 * @param dataDecode dataDecode 어노테이션이 달린 메소드
	 */
	@Pointcut("@annotation(dataDecode)")
	public void dataDecodePointcut(DataDecode dataDecode) {
	}

	/**
	 * DataDecode 어노테이션에 명시된 필드가 존재하는지 확인한 후, 존재한다면 해당 필드의 값을 디코딩합니다.
	 *
	 * @param dataDecode dataDecode 어노테이션이 달린 메소드
	 * @param result     dataDecode 어노테이션이 달린 메소드의 리턴값(객체)
	 * @return 디코딩된 필드를 가진 객체
	 */
	@AfterReturning(pointcut = "dataDecodePointcut(dataDecode)", returning = "result", argNames = "dataDecode,result")
	public Object dataDecode(DataDecode dataDecode, Object result) throws Throwable {
//		List<String> decodeTargets = Arrays.stream(dataDecode.value()).collect(Collectors.toList());
//		Field[] declaredFields = result.getClass().getDeclaredFields();
//
//		for (Field field : declaredFields) {
//			field.setAccessible(true);
//			if (decodeTargets.contains(field.getName())) {
//				String decodedData = DataDecoder.decode((String) field.get(result));
//				field.set(result, decodedData);
//			}
//		}
		return result;
	}
}
