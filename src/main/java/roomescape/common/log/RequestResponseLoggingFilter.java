package roomescape.common.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws ServletException, IOException {
        ContentCachingRequestWrapper cacheRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cacheResponse = new ContentCachingResponseWrapper(response);

        chain.doFilter(cacheRequest, cacheResponse);
        if (isNotLoggingUri(cacheRequest.getRequestURI())) {
            cacheResponse.copyBodyToResponse();
            return;
        }
        generateLogging(cacheRequest, cacheResponse);
    }

    private void generateLogging(ContentCachingRequestWrapper cacheRequest,
        ContentCachingResponseWrapper cacheResponse) throws IOException {
        HttpLogMessage logMessage = HttpLogMessage.createInstance(cacheRequest, cacheResponse);
        log.info(logMessage.toString());
        cacheResponse.copyBodyToResponse();
    }

    /**
     * 정적 페이지입니다.
     */
    private static final List<String> EXCLUDE_URI = Arrays.asList(
        "/",
        "/admin/reservation",
        "/admin/time",
        "/admin/theme",
        "/admin/waiting",
        "/reservation",
        "/reservation-mine",
        "/payment",
        "/login",
        "/signup"
    );


    private boolean isNotLoggingUri(String uri)  {
        return EXCLUDE_URI.contains(uri) || isStaticResource(uri);
    }

    private boolean isStaticResource(String uri) {
        return uri.endsWith(".css") || uri.endsWith(".js") ||
            uri.endsWith(".html") || uri.startsWith("/image");
    }
}
