package roomescape.global.error.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;
import roomescape.global.error.dto.ErrorResponse;
import roomescape.global.error.exception.ExternalApiClientException;
import roomescape.global.error.exception.ExternalApiServerException;
import roomescape.global.error.exception.WarningException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_LEVEL_WARN = "WARN";
    private static final String LOG_LEVEL_ERROR = "ERROR";

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(WarningException.class)
    public ResponseEntity<ErrorResponse> handleWarningException(WarningException ex, HttpServletRequest request) {
        String traceId = logErrorDetails(ex, request, LOG_LEVEL_WARN);
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(traceId, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String traceId = logErrorDetails(e, request, LOG_LEVEL_WARN);
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" / "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(traceId, errorMessage));
    }

    @ExceptionHandler(ExternalApiClientException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiClientException(
            ExternalApiClientException ex,
            HttpServletRequest request
    ) {
        String traceId = logErrorDetails(ex, request, LOG_LEVEL_WARN);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(traceId, ex.getMessage()));
    }

    @ExceptionHandler(ExternalApiServerException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiServerException(
            ExternalApiServerException ex,
            HttpServletRequest request
    ) {
        String traceId = logErrorDetails(ex, request, LOG_LEVEL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(traceId, ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e, HttpServletRequest request) {
        String traceId = logErrorDetails(e, request, LOG_LEVEL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(traceId, e.getMessage()));
    }

    /**
     * 실제 로깅 로직 모든 에러 핸들러에서 공통으로 호출하여 로그를 남깁니다.
     *
     * @param ex       발생한 예외 객체
     * @param request  HttpServletRequest 객체
     * @param logLevel 로깅할 레벨 (WARN 또는 ERROR)
     */
    private String logErrorDetails(Exception ex, HttpServletRequest request, String logLevel) {
        Map<String, Object> logData = new LinkedHashMap<>();
        // 에러 헤더
        logData.put("level", logLevel);
        logData.put("timestamp", Instant.now().toString());
        logData.put("traceId", MDC.get("traceId"));

        // 예외 정보
        logData.put("exceptionType", ex.getClass().getName());
        logData.put("errorMessage", ex.getMessage());
        // 스택 트레이스 전부를 로깅하면 용량 문제를 야기할 수 있음.
        // 이에 좀 더 고민하고 상위 몇줄만 남기는 방식, 또는 다른 방식을 생각해볼 것.
        // logData.put("stackTrace", ExceptionUtils.getStackTrace(ex));

        // 요청 정보
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String remoteAddr = request.getRemoteAddr();

        logData.put("requestUri", requestUri);
        logData.put("httpMethod", method);
        if (queryString != null && !queryString.isEmpty()) {
            logData.put("queryString", queryString);
        }
        // 추후 리버스 프록시 환경으로 변경시 remoteAddr를 X-Forwarded-For 헤더로 대체할 수 있음
        logData.put("remoteAddress", remoteAddr);

        // 요청 헤더
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            // TODO: 민감 정보 (인증 토큰 .etc) 마스킹
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        logData.put("requestHeaders", headers);

        // 요청 페이로드 (ContentCachingRequestWrapper를 통해 캐싱된 데이터 사용)
        String requestBody = "";
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper) request;
            byte[] buf = wrappedRequest.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    String characterEncoding = wrappedRequest.getCharacterEncoding();
                    if (characterEncoding == null) {
                        characterEncoding = StandardCharsets.UTF_8.name(); // 기본값 설정
                    }
                    requestBody = new String(buf, characterEncoding);
                    // TODO: 민감 정보 (사용자 비밀번호 .etc) 마스킹
                    // requestBody = maskSensitiveData(requestBody);
                } catch (Exception e) {
                    requestBody = "";
                }
            }
        }
        if (!requestBody.isEmpty()) {
            logData.put("requestPayload", requestBody);
        }

        // 최종 JSON 로깅
        try {
            String jsonLog = objectMapper.writeValueAsString(logData);
            if (LOG_LEVEL_WARN.equalsIgnoreCase(logLevel)) {
                log.warn(jsonLog);
            } else if (LOG_LEVEL_ERROR.equalsIgnoreCase(logLevel)) {
                log.error(jsonLog);
            }
        } catch (Exception jsonEx) {
            // JSON 변환 자체에 실패하면 일반 로그로 대체
            log.error("Failed to convert log data to JSON: {}", jsonEx.getMessage());
            log.error("Fallback log (Level: {}, Message: {})", logLevel, ex.getMessage(), ex);
        }
        return MDC.get("traceId");
    }
}
