package roomescape.infrastructure.thirdparty;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AuthHeaderGenerator {

    public String generateBasicAuthHeader(String apiKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = encoder.encodeToString((apiKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }
}
