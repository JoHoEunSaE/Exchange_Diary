package org.johoeunsae.exchangediary.utils.obfuscation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 난독화가 필요한 객체를 전달받는 메소드에 적용하는 어노테이션
 * <p>
 * 난독화가 필요한 Class 와 필드를 명시합니다. Ex) Diary 클래스의 content와 title 필드를 난독화할 경우
 *
 * @DataEncode({
 * @TargetMapping(clazz = Diary.class, fields = {TestDto.Fields.content, TestDto.Fields.title}) )}
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataEncode {

	TargetMapping[] value();
}
