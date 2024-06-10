package roomescape.auth.domain;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Token 객체", description = "토큰 값을 관리한다.")
public class Token {

    private final String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
