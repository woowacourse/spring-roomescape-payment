package roomescape.infrastructure.log;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import roomescape.infrastructure.security.AccessToken;
import roomescape.infrastructure.security.JwtProperties;
import roomescape.presentation.support.JwtTokenExtractor;

@Aspect
@Component
public final class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    private final JwtTokenExtractor jwtTokenExtractor;
    private final JwtProperties jwtProperties;

    public LogAspect(JwtTokenExtractor jwtTokenExtractor, JwtProperties jwtProperties) {
        this.jwtTokenExtractor = jwtTokenExtractor;
        this.jwtProperties = jwtProperties;
    }

    @Pointcut("execution(* roomescape.presentation..*Controller.*(..))")
    public void controllerMethods() {
    }

    @Pointcut("execution(* roomescape.application.reservation.command.*Service.*(..)) && !within(roomescape.application.reservation.command.AutoWaitingPromotionService)")
    public void serviceMethods() {
    }

    @Around("controllerMethods()")
    public Object logAroundControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = getSafeArgs(joinPoint);
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

    private Object[] getSafeArgs(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return Arrays.stream(args)
                .map(PasswordMasker::mask)
                .toArray();
    }

    @Around("serviceMethods()")
    public Object logAroundServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Long memberId = getMemberId();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        try {
            Object result = joinPoint.proceed();
            LogEntry responseLog = new InfoLog(memberId, className, methodName, LocalDateTime.now());
            LOGGER.info(responseLog.toLogMessage());
            return result;
        } catch (Throwable ex) {
            LogEntry errorLog = new ErrorLog(className, methodName, ex.getMessage(), ex);
            LOGGER.error(errorLog.toLogMessage());
            throw ex;
        }
    }

    private Long getMemberId() {
        try {
            HttpServletRequest request = currentHttpRequest();
            AccessToken accessToken = jwtTokenExtractor.extract(request);
            return accessToken.extractMemberId(jwtProperties.secretKey());
        } catch (Exception e) {
            return -1L; // 문제가 발생한 경우 -1을 반환
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
