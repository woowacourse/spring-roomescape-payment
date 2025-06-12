package roomescape.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LogAspect {
    @Pointcut("execution(* roomescape..*Controller.*(..))")
    public void controller() {
    }

    @Before("controller()")
    public void logBeforeApiCall(final JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = getMethod(joinPoint);
        log.info("api call : {}", method.getName());

        String[] paramNames =  methodSignature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        if (paramValues.length == 0) {
            log.info("params is empty");
        } else {
            for (int i = 0;i< paramValues.length;i++) {
                if (paramValues[i] == null) {
                    log.info("'{}' param value is null", paramNames[i]);
                }
            }
        }
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    @AfterReturning(pointcut = "controller()", returning = "result")
    public void logAfterApiCall(final JoinPoint joinPoint, final Object result) throws Throwable {
        Method method = getMethod(joinPoint);
        log.info("api return : {}", method.getName());
        log.info("return type : {}", result.getClass().getSimpleName());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonResult = objectMapper.writeValueAsString(result);
            log.info("return value in JSON : {}", jsonResult);
        } catch (Exception e) {
            log.error("Failed to convert result to JSON", e);
            log.info("original return value : {}", result);
        }
    }

    @AfterThrowing(pointcut = "controller()", throwing = "e")
    public void afterThrowingController(JoinPoint joinPoint, Throwable e) {
        Method method = getMethod(joinPoint);
        log.error("==========");
        log.error("Exception occurred in {}", method.getName());
        log.error("Exception message = {}", e.getMessage());
        log.error("Exception stacktrace = {}", e.getStackTrace());
        log.error("==========");
    }
}
