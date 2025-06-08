package roomescape.common.log.message;

import jakarta.servlet.http.HttpServletRequest;

public record RequestInfo(
        String id,
        String ip,
        String method,
        String url,
        String handlerName
) {

    public static RequestInfo get(
            final String id,
            final HttpServletRequest httpServletRequest,
            final String handlerName
    ) {
        return new RequestInfo(
                id,
                httpServletRequest.getRemoteAddr(),
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                handlerName
        );
    }
}
