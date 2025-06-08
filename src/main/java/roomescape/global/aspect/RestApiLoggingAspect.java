package roomescape.global.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RestApiLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RestApiLoggingAspect.class);

    @Around("@within(restController)")
    public Object logUrlAndStatus(ProceedingJoinPoint joinPoint, RestController restController) throws Throwable {
        String method = extractHttpMethod();
        String path = extractRequestUri();

        Object result = joinPoint.proceed();

        int status = extractResponseStatus();

        log.info("[{}] {} => status: {}", method, path, status);
        return result;
    }

    private HttpServletRequest extractRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest();
        }
        return null;
    }

    private HttpServletResponse extractResponse() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getResponse();
        }
        return null;
    }

    private String extractRequestUri() {
        HttpServletRequest request = extractRequest();
        if (request != null) {
            return request.getRequestURI();
        }
        return "N/A";
    }

    private String extractHttpMethod() {
        HttpServletRequest request = extractRequest();
        if (request != null) {
            return request.getMethod();
        }
        return "N/A";
    }

    private int extractResponseStatus() {
        HttpServletResponse response = extractResponse();
        if (response != null) {
            return response.getStatus();
        }
        return -1;
    }
}
