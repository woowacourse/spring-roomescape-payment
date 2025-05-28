package roomescape.global.api;

public abstract class AuthToken {
    private final String token;

    public AuthToken(final String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    abstract String generateToken();
}
