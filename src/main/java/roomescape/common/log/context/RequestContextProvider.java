package roomescape.common.log.context;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class RequestContextProvider {

    private final RequestContext requestContext;

    protected RequestContextProvider(HttpServletRequest httpServletRequest) {
        this.requestContext = new RequestContext(
                UUID.randomUUID().toString(),
                httpServletRequest.getRemoteAddr(),
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI()
        );
    }

    public RequestContext get() {
        return requestContext;
    }
}
