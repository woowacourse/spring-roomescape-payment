package roomescape.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* roomescape.controller..*Controller.*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = requestAttributes.getRequest();
        String method = request.getMethod();

        Map<String, Object> requestLogData = new HashMap<>();
        requestLogData.put("Method", request.getMethod());
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUri = requestURI + (queryString == null ? "" : "?" + queryString);
        requestLogData.put("URI", fullUri);
        requestLogData.put("ARGS", joinPoint.getArgs());

        ObjectMapper objectMapper = new ObjectMapper();
        log.info(objectMapper.writeValueAsString(requestLogData));

        try {
            Object result = joinPoint.proceed();

            long elapsed = System.currentTimeMillis() - start;
            Map<String, Object> responseLogData = new HashMap<>();
            responseLogData.put("Method", method);
            responseLogData.put("Time", elapsed + " ms");
            responseLogData.put("Result", result);
            log.info(objectMapper.writeValueAsString(responseLogData));
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            Map<String, Object> errorLogData = new HashMap<>();
            errorLogData.put("Method", method);
            errorLogData.put("Time", elapsed + " ms");
            errorLogData.put("Message", e.getMessage());
            log.error(objectMapper.writeValueAsString(errorLogData));
            throw e;
        }
    }
}
