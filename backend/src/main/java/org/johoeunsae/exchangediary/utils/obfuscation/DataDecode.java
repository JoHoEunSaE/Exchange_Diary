package org.johoeunsae.exchangediary.utils.obfuscation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 난독화된 객체를 가독화하는 어노테이션 ex) @DataDecode({"content", "title"})
 */

//@Deprecated

/**
 * // * @deprecated Use {@link org.johoeunsae.exchangediary.utils.obfuscation.DecodeSerializer}
 * instead use dto field upper annotation @JsonSerialize(using = DecodeSerializer.class) instead
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataDecode {

//	String[] value();
}
