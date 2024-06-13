package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token Response Model")
public record TokenResponse(@Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                            String accessToken) {

    public static TokenResponse from(String accessToken) {
        return new TokenResponse(accessToken);
    }
}
