package roomescape.business.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.exception.member.UserNameFormatException;
import roomescape.exception.member.UserNameLengthException;

@Embeddable
public record UserName(
        @Column(name = "name")
        String value
) {
    private static final int MAX_LENGTH = 10;

    public UserName {
        validateMaxLength(value);
        validateNameDoesNotContainsNumber(value);
    }

    private static void validateMaxLength(final String name) {
        if (name.length() > MAX_LENGTH) {
            throw new UserNameLengthException(MAX_LENGTH);
        }
    }

    private static void validateNameDoesNotContainsNumber(final String name) {
        for (char c : name.toCharArray()) {
            if (Character.isDigit(c)) {
                throw new UserNameFormatException();
            }
        }
    }
}
