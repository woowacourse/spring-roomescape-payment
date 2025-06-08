package roomescape.common.log.context;

import jakarta.servlet.http.HttpServletRequest;

public record RequestContext(
        String ip,
        String method,
        String url,
        String handlerName
) {

    public static RequestContext get(final HttpServletRequest httpServletRequest, final String handlerName) {
        return new RequestContext(
                httpServletRequest.getRemoteAddr(),
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                handlerName
        );
    }
}
