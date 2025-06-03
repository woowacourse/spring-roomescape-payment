package roomescape.domain.auth;

public interface AuthPasswordEncoder {
    boolean matches(final String notEncodedPassword, final String password);
}
