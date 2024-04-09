package org.johoeunsae.exchangediary.utils.obfuscation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 객체 난독화가 필요한 클래스에 적용하는 어노테이션
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Decodable {

}
