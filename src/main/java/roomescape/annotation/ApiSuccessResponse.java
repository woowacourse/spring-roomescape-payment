package roomescape.annotation;

import static org.springframework.http.HttpStatus.OK;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.HttpStatus;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSuccessResponse {
    HttpStatus status() default OK;

    String body() default "";

    Class<?> bodyType() default Void.class;
}
