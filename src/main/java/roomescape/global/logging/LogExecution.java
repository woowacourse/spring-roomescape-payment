package roomescape.global.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {
    LogLevel level() default LogLevel.INFO;
    LogContent[] content() default {LogContent.REQUEST, LogContent.RESPONSE, LogContent.EXECUTION_TIME};
    String description() default "";
    boolean maskSensitiveData() default true;
}
