package roomescape.domain;

import org.springframework.http.HttpStatus;
import roomescape.application.exception.AuthException;

import java.util.Arrays;

public enum Role {

    ADMIN,
    USER
    ;

    public static Role from(String name) {
        return Arrays.stream(values())
                .filter(role -> role.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new AuthException("[ERROR] 유효하지 않은 권한입니다.", HttpStatus.UNAUTHORIZED));
    }
}
