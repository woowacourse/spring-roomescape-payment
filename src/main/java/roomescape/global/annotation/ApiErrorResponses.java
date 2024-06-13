package roomescape.global.annotation;

import roomescape.exception.ErrorType;
import roomescape.exception.ErrorTypeGroup;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({METHOD, TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiErrorResponses {
    ErrorType[] value() default {};
    ErrorTypeGroup[] groups() default {};
}

