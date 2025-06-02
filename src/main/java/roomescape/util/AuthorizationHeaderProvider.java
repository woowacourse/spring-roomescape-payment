package roomescape.util;

import java.util.Base64;

public class AuthorizationHeaderProvider {
    public String provide(String value) {
        return "Basic " + Base64.getEncoder().encodeToString(value.getBytes());
    }
}
