package roomescape.global.api;

import java.util.Collections;
import java.util.List;

public class CustomRequestUri {
    private final String path;
    private final List<Object> params;

    public CustomRequestUri(final String uri) {
        this.path = uri;
        this.params = Collections.emptyList();
    }

    public CustomRequestUri(final String uri, final List<Object> params) {
        this.path = uri;
        this.params = params;
    }

    public String getUriPath() {
        return path;
    }
}
