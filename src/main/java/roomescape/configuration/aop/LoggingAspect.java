package roomescape.configuration.aop;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* roomescape..*.*(..))")
    public void allInPackage() {
    }

    @Pointcut("execution(* roomescape.configuration.aop..*.*(..))")
    public void allInAopPackage() {
    }

    @Pointcut("allInPackage() && !allInAopPackage()")
    public void allWithoutAopPackage() {
    }

    @Pointcut("execution(* roomescape.controller..*.*(..))")
    public void allController() {

    }

    @Pointcut("execution(* roomescape.service..*.*(..))")
    public void allService() {

    }

    @Pointcut("execution(* roomescape.repository..*.*(..))")
    public void allRepository() {

    }

    // [TODO] : HTTP 요청과 응답 로그 (INFO 레벨)
    @Around("allController()")
    public Object logAllRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        List<String> args = Arrays.stream(joinPoint.getArgs()).map(Object::toString).toList();
        log.info("[INFO][{}][IN][{}] {}", LocalDateTime.now(), joinPoint.getSignature(), String.join(",", args));
        Object returnValue = joinPoint.proceed();
        log.info("[INFO][{}][OUT][{}] {}", LocalDateTime.now(), joinPoint.getSignature(), returnValue);
        return returnValue;
    }

    // [TODO] : 컨트롤러/서비스/레파지토리 메서드에 대한 인자와 리턴값
    @Around("allController() || allService() || allRepository()")
    public Object logLayerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        List<String> args = Arrays.stream(joinPoint.getArgs()).map(Object::toString).toList();
        log.debug("[DEBUG][{}][IN][{}] {}", LocalDateTime.now(), joinPoint.getSignature(), String.join(",", args));
        Object returnValue = joinPoint.proceed();
        log.debug("[DEBUG][{}][OUT][{}] {}", LocalDateTime.now(), joinPoint.getSignature(), returnValue);
        return returnValue;
    }

    // [TODO] : 작성된 모든 메서드에 대한 인자와 리턴값 (TRACE 레벨)
    @Around("allWithoutAopPackage()")
    public Object logAllMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        List<String> args = Arrays.stream(joinPoint.getArgs()).map(Object::toString).toList();
        log.trace("[TRACE][{}][IN][{}] {}", LocalDateTime.now(), joinPoint.getSignature(), String.join(",", args));
        Object returnValue = joinPoint.proceed();
        log.trace("[TRACE][{}][OUT][{}] {}", LocalDateTime.now(), joinPoint.getSignature(), returnValue);
        return returnValue;
    }
}
