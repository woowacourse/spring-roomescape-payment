package roomescape.exception.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PaymentLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(PaymentLoggingAspect.class);

    @Around("@annotation(roomescape.exception.aspect.PaymentLogging)")
    public Object logPaymentExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String parameterInfo = getParameterInfo(joinPoint);

        long startTime = System.currentTimeMillis();

        try {
            logger.info("{}.{} 요청", className, methodName);
            Object result = joinPoint.proceed();
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("{}.{} [파라미터 정보] : {} 실패 - 오류: {}, 소요시간: {}ms", className, methodName, parameterInfo,
                    e.getMessage(), executionTime);
            throw e;
        }
    }

    private String getParameterInfo(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = getParameterNames(joinPoint);

        if (args == null || args.length == 0) {
            return "파라미터 없음";
        }

        StringBuilder paramInfo = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                paramInfo.append(", ");
            }
            if (args[i] == null) {
                continue;
            }

            String paramName = (parameterNames != null && i < parameterNames.length)
                    ? parameterNames[i]
                    : "param" + i;

            String paramValue = formatParameterValue(args[i].toString());
            paramInfo.append(paramName).append("=").append(paramValue);
        }

        return paramInfo.toString();
    }

    private String formatParameterValue(String param) {
        String value = param.toString();
        if (value.length() > 200) {
            return value.substring(0, 200) + "...[truncated]";
        }
        return value;
    }

    private String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            return signature.getParameterNames();
        } catch (Exception e) {
            return null;
        }
    }
}
