package roomescape.util;

import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHeaderProvider {

    public String provide(String value) {
        return "Basic " + Base64.getEncoder().encodeToString(value.getBytes());
    }
}
