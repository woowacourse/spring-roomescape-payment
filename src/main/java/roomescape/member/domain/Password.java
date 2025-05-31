package roomescape.member.domain;

import jakarta.persistence.Embeddable;
import roomescape.common.exception.BadRequestException;

@Embeddable
public record Password(String value) {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 16;

    public Password {
        validate(value);
    }

    private void validate(final String password) {
        validateBlank(password);
        validateLength(password);
    }

    private void validateBlank(final String password) {
        if (password == null || password.isBlank()) {
            throw new BadRequestException("값이 존재하지 않습니다.");
        }
    }

    private void validateLength(final String password) {
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            throw new BadRequestException("비밀번호는 %d자 이상 %d자 이하로 가능합니다.".formatted(
                    MIN_LENGTH, MAX_LENGTH
            ));
        }
    }
}
