package roomescape.log;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class LoggingPolicy {

    private static final Set<String> ALWAYS_LOG_METHODS = Set.of(
            "POST", "PUT", "DELETE", "PATCH"
    );

    public boolean shouldLog(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (ALWAYS_LOG_METHODS.contains(method)) {
            return true;
        }
        if ("GET".equals(method)) {
            return shouldLogGetRequest(uri);
        }

        return true;
    }

    private boolean shouldLogGetRequest(String uri) {
        if (uri.startsWith("/admin/")) {
            return true;
        }

        if (uri.contains("/login/check") || uri.contains("/logout")) {
            return true;
        }

        return false;
    }
}