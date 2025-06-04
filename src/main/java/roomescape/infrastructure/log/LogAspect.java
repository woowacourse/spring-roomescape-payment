package roomescape.infrastructure.log;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public final class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(* roomescape.presentation..*Controller.*(..))")
    public void controllerMethods() {
    }

    @Pointcut("execution(* roomescape.infrastructure.error.*ExceptionHandler.*(..))")
    public void exceptionHandlerMethods() {
    }

    @Around("controllerMethods()")
    public Object logAroundControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = currentHttpRequest();
        LogEntry requestLog = buildRequestLog(className, methodName, args, request);
        LOGGER.info(requestLog.toLogMessage());
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            LogEntry responseLog = buildResponseLog(className, methodName, result, endTime - startTime);
            LOGGER.info(responseLog.toLogMessage());
            return result;
        } catch (Throwable ex) {
            LogEntry errorLog = new ErrorLog(className, methodName, ex.getMessage(), ex);
            LOGGER.error(errorLog.toLogMessage());
            throw ex;
        }
    }

    private HttpServletRequest currentHttpRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
    }

    private LogEntry buildRequestLog(String className, String methodName, Object[] args, HttpServletRequest request) {
        String httpMethod = request.getMethod();
        String uri = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);
        Map<String, String> params = extractRequestParameters(request);
        return new RequestLog(httpMethod, uri, className, methodName, args, params);
    }

    private LogEntry buildResponseLog(String className, String methodName, Object result, long duration) {
        return new ResponseLog(className, methodName, result, duration);
    }

    private Map<String, String> extractRequestParameters(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            paramMap.put(name, request.getParameter(name));
        }
        return paramMap;
    }
}
