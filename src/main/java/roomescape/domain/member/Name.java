package roomescape.domain.member;

import static roomescape.exception.ErrorCode.USER_NAME_FORMAT_ERROR;
import static roomescape.exception.ErrorCode.USER_NAME_LENGTH_ERROR;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import roomescape.exception.RoomEscapeException;

@Embeddable
public class Name {

    private static final String NAME_PATTERN = "^[a-zA-Zㄱ-ㅎ가-힣]*$";
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 10;

    private String name;

    protected Name() {
    }

    public Name(String name) {
        validateName(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void validateName(String name) {
        validateNameFormat(name);
        validateNameLength(name);
    }

    private void validateNameFormat(String name) {
        if (!Pattern.matches(NAME_PATTERN, name)) {
            throw new RoomEscapeException(
                    USER_NAME_FORMAT_ERROR,
                    "member_name = " + name
            );
        }
    }

    private void validateNameLength(String name) {
        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new RoomEscapeException(
                    USER_NAME_LENGTH_ERROR,
                    "member_name = " + name
            );
        }
    }
}
