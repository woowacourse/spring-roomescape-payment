package roomescape.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Slf4j
@Component
public class LogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MASK_VALUE = "*********";

    @Pointcut("execution(* roomescape.controller..*Controller.*(..))")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String clientIP = getClientIP(request);
                String method = request.getMethod();
                String uri = request.getRequestURI();
                String queryString = request.getQueryString();
                String fullUrl = queryString != null ? uri + "?" + queryString : uri;

                String requestBody = getRequestBodyWithMasking(joinPoint, method);

                if (requestBody != null && !requestBody.isEmpty()) {
                    log.info("[{}] {} - {} | Body: {}", method, fullUrl, clientIP, requestBody);
                } else {
                    log.info("[{}] {} - {}", method, fullUrl, clientIP);
                }
            }
        } catch (Exception e) {
            log.error("로그 처리 중 오류 발생", e);
        }
    }

    private String getRequestBodyWithMasking(JoinPoint joinPoint, String httpMethod) {
        if ("GET".equals(httpMethod) || "DELETE".equals(httpMethod)) {
            return null;
        }

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }

        for (Object arg : args) {
            if (arg != null && !isPrimitiveOrWrapper(arg) && !isHttpServletRequest(arg)) {
                return maskSensitiveFields(arg);
            }
        }

        return null;
    }

    private String maskSensitiveFields(Object obj) {
        try {
            String jsonString = objectMapper.writeValueAsString(obj);
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(jsonString);

            Class<?> cls = obj.getClass();
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Sensitive.class)) {
                    String fieldName = field.getName();
                    if (jsonNode.has(fieldName)) {
                        jsonNode.put(fieldName, MASK_VALUE);
                    }
                }
            }
            return jsonNode.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private boolean isPrimitiveOrWrapper(Object obj) {
        return obj instanceof String ||
                obj instanceof Number ||
                obj instanceof Boolean ||
                obj instanceof Character ||
                obj.getClass().isPrimitive();
    }

    private boolean isHttpServletRequest(Object obj) {
        return obj instanceof HttpServletRequest;
    }

    private String getClientIP(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.isEmpty()) {
            clientIP = request.getRemoteAddr();
        }
        return clientIP != null ? clientIP : "unknown";
    }
}
