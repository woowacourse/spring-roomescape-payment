package roomescape.global.annotation;

import roomescape.exception.ErrorType;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ANNOTATION_TYPE, METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponse {
    ErrorType value();
}
