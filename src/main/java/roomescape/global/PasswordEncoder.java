package roomescape.global;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class PasswordEncoder {

    public String encode(final String rawPassword) {
        return Base64.getEncoder().encodeToString(rawPassword.getBytes());
    }

    public boolean matches(final String rawPassword, final String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
