package roomescape.common.log.context;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public record RequestContext(
        String id,
        String ip,
        String method,
        String url
) {

    /**
     * 현재 요청 쓰레드에 바인딩된 RequestContext를 반환합니다.
     * <p>
     * 이 메서드는 Spring MVC 에서 요청을 처리하는 동안에만 유효합니다.
     * <br>
     * 이 메서드를 호출하는 시점에 현재 쓰레드에 요청이 바인딩되어 있어야 합니다.
     * <br>
     * 만약 현재 쓰레드에 요청이 바인딩되어 있지 않다면, {@link IllegalStateException}을 발생시킵니다.
     *
     * @return 현재 요청에 대한 RequestContext
     * @throws IllegalStateException 현재 쓰레드에 요청이 바인딩되어 있지 않은 경우
     */
    public static RequestContext fromCurrentRequest() {
        final ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            throw new IllegalStateException("No request bound to current thread");
        }
        final HttpServletRequest request = servletRequestAttributes.getRequest();
        return new RequestContext(
                generateId(),
                request.getRemoteAddr(),
                request.getMethod(),
                request.getRequestURI()
        );
    }

    public static RequestContext fromHttpServletRequest(
            final HttpServletRequest httpServletRequest
    ) {
        return new RequestContext(
                generateId(),
                httpServletRequest.getRemoteAddr(),
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI()
        );
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}
