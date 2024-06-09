package roomescape.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import roomescape.exception.RoomescapeException;
import roomescape.exception.RoomescapeExceptionType;

@Schema(description = "회원 등급", example = "role1")
public enum Role {
    ADMIN("role1"),
    USER("role2");

    private final String tokenValue;

    Role(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public static Role findByValue(String value) {
        return Arrays.stream(values())
                .filter(role -> role.tokenValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new RoomescapeException(RoomescapeExceptionType.NOT_FOUND_ROLE));
    }

    public boolean isAdmin() {
        return this == Role.ADMIN;
    }


    public String getTokenValue() {
        return tokenValue;
    }
}
