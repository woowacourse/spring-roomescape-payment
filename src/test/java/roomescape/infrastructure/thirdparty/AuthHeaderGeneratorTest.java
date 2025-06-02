package roomescape.infrastructure.thirdparty;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthHeaderGeneratorTest {

    @Test
    void 인증_헤더를_생성한다() {
        String apiKey = "api-key";
        AuthHeaderGenerator authHeaderGenerator = new AuthHeaderGenerator();

        String generatedBasicAuthHeader = authHeaderGenerator.generateBasicAuthHeader(apiKey);

        String expected = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes(StandardCharsets.UTF_8));
        assertThat(generatedBasicAuthHeader).isEqualTo(expected);
    }
}
