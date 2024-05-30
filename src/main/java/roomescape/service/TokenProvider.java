package roomescape.service;

public interface TokenProvider {

    String createToken(String subject);

    String extractSubject(String token);
}
