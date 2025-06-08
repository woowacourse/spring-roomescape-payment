package roomescape.common.log.context;

import jakarta.servlet.http.HttpServletRequest;

public record RequestContext(
        String id,
        String ip,
        String method,
        String url,
        String handlerName
) {

    public static RequestContext get(
            final String id,
            final HttpServletRequest httpServletRequest,
            final String handlerName
    ) {
        return new RequestContext(
                id,
                httpServletRequest.getRemoteAddr(),
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                handlerName
        );
    }
}
