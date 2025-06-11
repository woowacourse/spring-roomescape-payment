package roomescape.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {
    private long maxAge;

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
    }
}
