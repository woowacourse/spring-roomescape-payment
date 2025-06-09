package roomescape.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    public LoggingAspect(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("within(roomescape.controller.api..*)")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void beforeLog(JoinPoint joinPoint) throws JsonProcessingException {
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        Map<String, Object> requestLogInfos = new LinkedHashMap<>();
        requestLogInfos.put("Log Type", "Request");
        requestLogInfos.put("Methods", request.getMethod());
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUri = requestURI + (queryString == null ? "" : "?" + queryString);
        requestLogInfos.put("Uri", fullUri);
        requestLogInfos.put("Args", Arrays.toString(joinPoint.getArgs()));

        if (log.isInfoEnabled()) {
            log.info(objectMapper.writeValueAsString(requestLogInfos));
        }
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void afterLog(JoinPoint joinPoint, Object result) throws JsonProcessingException {
        try {
            Map<String, Object> responseLogInfos = new LinkedHashMap<>();
            responseLogInfos.put("Log Type", "Response");
            responseLogInfos.put("Method", joinPoint.getSignature().getName());
            long elapsed = System.currentTimeMillis() - startTime.get();
            responseLogInfos.put("Time", elapsed + "ms");
            responseLogInfos.put("Result", result);
            if (log.isInfoEnabled()) {
                log.info(objectMapper.writeValueAsString(responseLogInfos));
            }
        } finally {
            startTime.remove();
        }
    }
}
