package roomescape.infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Aspect
@Component
public class BusinessLogicLogger {

    private final ObjectMapper objectMapper;

    public BusinessLogicLogger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Around("execution(* roomescape.presentation.api.*Controller.*(..)) "
            + "|| execution(* roomescape.presentation.web.*Controller.*(..)) "
            + "|| execution(* roomescape.application.*Service.*(..)) "
            + "|| execution(* roomescape.domain.*.*(..)) "
            + "|| execution(* roomescape.infrastructure.repository.*Repository.*(..))")
    public Object logBusinessLogic(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = extractClassName(joinPoint);
        String methodName = extractMethodName(joinPoint);
        String businessType = determineBusinessType(className);

        HttpServletRequest request = currentRequest();
        if (isController(className) && request instanceof ContentCachingRequestWrapper) {
            logRequestBody((ContentCachingRequestWrapper) request);
        }

        logInvocation(request, className, methodName, businessType);
        return joinPoint.proceed();
    }

    private String extractClassName(ProceedingJoinPoint jp) {
        return jp.getSignature().getDeclaringTypeName();
    }

    private String extractMethodName(ProceedingJoinPoint jp) {
        return jp.getSignature().getName();
    }

    private boolean isController(String className) {
        return className.contains("Controller");
    }

    private String determineBusinessType(String className) {
        if (className.contains("Controller")) {
            return "<<Controller>>";
        }
        if (className.contains("Service")) {
            return "<<Service>>";
        }
        if (className.contains("Repository")) {
            return "<<Repository>>";
        }
        return "";
    }

    private void logInvocation(HttpServletRequest request,
                               String className,
                               String methodName,
                               String type) {
        String requestId = "N/A";
        if (request != null) {
            Object attr = request.getAttribute("requestId");
            if (attr != null) {
                requestId = attr.toString();
            }
        }
        log.info("RequestId = {} {} {}.{}()", requestId, type, className, methodName);
    }

    private void logRequestBody(ContentCachingRequestWrapper wrapper) {
        String prettyJson = extractRequestBody(wrapper);
        String requestId = "N/A";
        Object attr = wrapper.getAttribute("requestId");
        if (attr != null) {
            requestId = attr.toString();
        }
        log.info("RequestId = {} <<Request>> Body =\n{}", requestId, prettyJson);
    }

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest() : null;
    }

    private String extractRequestBody(ContentCachingRequestWrapper wrapper) {
        try {
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length == 0) {
                return "";
            }
            return objectMapper.readTree(content).toPrettyString();
        } catch (Exception e) {
            return "";
        }
    }
}
