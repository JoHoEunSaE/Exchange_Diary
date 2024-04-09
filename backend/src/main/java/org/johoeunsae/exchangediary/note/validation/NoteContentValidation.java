package org.johoeunsae.exchangediary.note.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;


@Constraint(validatedBy = {})
@Size(min = 1, max = 4095, message = "일기장 내용은 4095자 이하로 작성해 주세요.")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoteContentValidation {

	String message() default "일기장 내용이 올바르지 않습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
