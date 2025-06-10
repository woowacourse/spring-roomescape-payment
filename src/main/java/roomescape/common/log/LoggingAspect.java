package roomescape.common.log;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import roomescape.common.log.context.RequestContext;
import roomescape.common.log.entry.ErrorLogEntry;
import roomescape.common.log.entry.RequestLogEntry;
import roomescape.common.log.entry.ResponseLogEntry;
import roomescape.common.log.message.LogMessageProvider;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final String HANDLER_NAME_FORMAT = "%s#%s";

    private final LogMessageProvider logMessageProvider;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    private void getMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    private void postMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    private void putMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    private void deleteMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    private void patchMapping() {
    }

    @Pointcut("getMapping() || postMapping() || putMapping() || deleteMapping() || patchMapping()")
    private void allMapping() {
    }

    @Pointcut("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    private void controllerPointCut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    private void exceptionHandlerCut() {
    }

    @Before("allMapping()")
    public void requestLog(final JoinPoint joinPoint) {
        final RequestLogEntry requestLogEntry = RequestLogEntry.createWithHandlerArguments(
                getRequestContext(),
                getHandlerName(joinPoint),
                getHandlerArguments(joinPoint)
        );
        log.info(logMessageProvider.getRequestLog(requestLogEntry));
    }

    @AfterReturning(value = "exceptionHandlerCut()", returning = "response")
    public void exceptionHandlerLog(final JoinPoint joinPoint, final ResponseEntity<?> response) {
        final ErrorLogEntry errorLogEntry = Arrays.stream(joinPoint.getArgs())
                .filter(Throwable.class::isInstance)
                .map(Throwable.class::cast)
                .map(arg -> ErrorLogEntry.withThrowable(getRequestContext(), arg, response))
                .findFirst()
                .orElse(ErrorLogEntry.withoutThrowable(getRequestContext(), response));

        log.warn(logMessageProvider.getErrorLog(errorLogEntry));
    }

    @AfterReturning(value = "controllerPointCut()", returning = "response")
    public void responseLog(final ResponseEntity<?> response) {
        final ResponseLogEntry responseLogEntry = new ResponseLogEntry(
                getRequestContext(),
                response
        );
        log.info(logMessageProvider.getResponseLog(responseLogEntry));
    }

    private String getHandlerName(final JoinPoint joinPoint) {
        return String.format(
                HANDLER_NAME_FORMAT,
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName()
        );
    }

    private Map<String, Object> getHandlerArguments(final JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        return IntStream.range(0, parameterNames.length)
                .boxed()
                .collect(Collectors.toMap(i -> parameterNames[i], i -> parameterValues[i]));
    }

    private RequestContext getRequestContext() {
        return RequestContext.fromCurrentRequest();
    }
}
