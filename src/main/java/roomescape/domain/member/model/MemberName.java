package roomescape.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.domain.member.exception.InvalidSignUpInputException;

@Embeddable
public class MemberName {

    private static final int MAXIMUM_ENABLE_NAME_LENGTH = 8;

    @Column(length = 20, nullable = false)
    private String name;

    protected MemberName() {
    }

    public MemberName(final String name) {
        validateValue(name);
        this.name = name;
    }

    private void validateValue(final String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidSignUpInputException("회원 이름으로 공백을 입력할 수 없습니다.");
        }

        if (value.length() > MAXIMUM_ENABLE_NAME_LENGTH) {
            throw new InvalidSignUpInputException("회원 이름은 " + MAXIMUM_ENABLE_NAME_LENGTH + "글자 이하여만 합니다.");
        }
    }

    @Override
    public String toString() {
        return "MemberName{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getValue() {
        return name;
    }
}
