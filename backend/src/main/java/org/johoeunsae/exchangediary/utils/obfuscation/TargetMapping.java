package org.johoeunsae.exchangediary.utils.obfuscation;

/**
 * 난독화가 필요한 class와 fields를 명시하는 어노테이션
 * <p>
 * class: 난독화가 필요한 필드를 가진 객체.class fields: 난독화가 필요한 객체의 필드 명
 */
public @interface TargetMapping {

	Class<?> clazz();

	String[] fields() default {};
}
