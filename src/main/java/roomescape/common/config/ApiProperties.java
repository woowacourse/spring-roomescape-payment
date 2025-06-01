package roomescape.common.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ApiProperties {
    private String baseUrl;
    private String secretKey;
    private int connectTimeout;
    private int readTimeout;
}
