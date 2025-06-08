package roomescape.global.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {
    
    /**
     * 로깅 레벨 지정
     */
    LogLevel level() default LogLevel.INFO;
    
    /**
     * 로깅할 내용 지정
     */
    LogContent[] content() default {LogContent.REQUEST, LogContent.RESPONSE, LogContent.EXECUTION_TIME};
    
    /**
     * 로깅 시 표시할 설명
     */
    String description() default "";
    
    /**
     * 민감한 정보 마스킹 여부
     */
    boolean maskSensitiveData() default true;
    
    enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    
    enum LogContent {
        REQUEST,        // 요청 파라미터
        RESPONSE,       // 응답 결과
        EXECUTION_TIME, // 실행 시간
        EXCEPTION,      // 예외 발생 시
        USER_ACTION     // 사용자 액션 (로그인, 결제 등)
    }
}
