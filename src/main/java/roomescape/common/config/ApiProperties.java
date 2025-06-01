package roomescape.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiProperties {
    private final String baseUrl;
    private final int connectTimeout;
    private final int readTimeout;
}
