package roomescape.global.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {
    
    private final ObjectMapper objectMapper;
    
    @Around("@annotation(LogExecution)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogExecution logExecution = method.getAnnotation(LogExecution.class);
        
        long startTime = System.currentTimeMillis();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        
        // 요청 정보 수집
        Map<String, Object> logData = new HashMap<>();
        logData.put("method", methodName);
        logData.put("description", logExecution.description().isEmpty() ? methodName : logExecution.description());
        
        // HTTP 요청 정보 수집 (웹 요청인 경우)
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            logData.put("httpMethod", request.getMethod());
            logData.put("uri", request.getRequestURI());
            logData.put("clientIp", getClientIp(request));
        }
        
        // 요청 파라미터 로깅
        if (Arrays.asList(logExecution.content()).contains(LogContent.REQUEST)) {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                logData.put("requestParams", maskSensitiveData(args, logExecution.maskSensitiveData()));
            }
        }
        
        // 사용자 액션 로깅 (세션에서 사용자 정보 추출)
        if (Arrays.asList(logExecution.content()).contains(LogContent.USER_ACTION)) {
            Long userId = getUserIdFromSession();
            if (userId != null) {
                logData.put("userId", userId);
            }
        }
        
        Object result = null;
        Exception exception = null;
        
        try {
            result = joinPoint.proceed();
            
            // 응답 결과 로깅
            if (Arrays.asList(logExecution.content()).contains(LogContent.RESPONSE)) {
                logData.put("response", maskSensitiveData(result, logExecution.maskSensitiveData()));
            }
            
            return result;
        } catch (Exception e) {
            exception = e;
            
            // 예외 로깅
            if (Arrays.asList(logExecution.content()).contains(LogContent.EXCEPTION)) {
                logData.put("exception", e.getClass().getSimpleName());
                logData.put("errorMessage", e.getMessage());
            }
            
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            
            // 실행 시간 로깅
            if (Arrays.asList(logExecution.content()).contains(LogContent.EXECUTION_TIME)) {
                logData.put("executionTime", endTime - startTime + "ms");
            }
            
            logData.put("status", exception == null ? "SUCCESS" : "FAILED");
            
            // 로그 레벨에 따른 출력
            String logMessage = formatLogMessage(logData);
            
            switch (logExecution.level()) {
                case LogLevel.DEBUG -> log.debug(logMessage);
                case LogLevel.INFO -> log.info(logMessage);
                case LogLevel.WARN -> log.warn(logMessage);
                case LogLevel.ERROR -> log.error(logMessage);
            }
        }
    }
    
    private HttpServletRequest getCurrentHttpRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (IllegalStateException e) {
            return null; // 웹 요청이 아닌 경우
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
    
    private Long getUserIdFromSession() {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request != null && request.getSession(false) != null) {
                return (Long) request.getSession().getAttribute("id");
            }
        } catch (Exception e) {
            // 세션 정보 조회 실패 시 무시
        }
        return null;
    }
    
    private Object maskSensitiveData(Object data, boolean shouldMask) {
        if (!shouldMask || data == null) {
            return data;
        }
        
        try {
            String json = objectMapper.writeValueAsString(data);
            json = json.replaceAll("(\"password\"\\s*:\\s*\")[^\"]*\"", "$1***\"");
            json = json.replaceAll("(\"paymentKey\"\\s*:\\s*\")[^\"]*\"", "$1***\"");
            json = json.replaceAll("(\"orderId\"\\s*:\\s*\")[^\"]*\"", "$1***\"");
            json = json.replaceAll("(\"cardNumber\"\\s*:\\s*\")[^\"]*\"", "$1****-****-****-****\"");
            return json;
        } catch (JsonProcessingException e) {
            return data.toString();
        }
    }
    
    private String formatLogMessage(Map<String, Object> logData) {
        try {
            return objectMapper.writeValueAsString(logData);
        } catch (JsonProcessingException e) {
            return logData.toString();
        }
    }
}
