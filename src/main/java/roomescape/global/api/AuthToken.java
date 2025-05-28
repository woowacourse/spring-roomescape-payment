package roomescape.global.api;

public abstract class AuthToken {
    private final String token;

    protected AuthToken(final String token) {
        this.token = token;
    }

    protected String getToken() {
        return token;
    }

    abstract String generateToken();
}
