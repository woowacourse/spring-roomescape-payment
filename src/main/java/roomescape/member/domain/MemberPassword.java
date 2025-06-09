package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import roomescape.global.exception.BadRequestException;

@Embeddable
@Slf4j
public record MemberPassword(
        @Column(nullable = false)
        @Size(max = MemberPassword.MAXIMUM_PASSWORD_LENGTH)
        String password
) {
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[A-Za-z]");

    private static final int MAXIMUM_PASSWORD_LENGTH = 30;
    private static final int MINIMUM_DIGIT_COUNT = 2;
    private static final int MINIMUM_SPECIAL_CHAR_COUNT = 1;
    private static final int MINIMUM_LETTER_COUNT = 5;

    public MemberPassword(final String password) {
        this.password = Objects.requireNonNull(password, "비밀번호가 유효하지 않습니다.");
        if (password.isBlank() || password.length() > MAXIMUM_PASSWORD_LENGTH) {
            log.warn("[VALIDATION-FAIL] 비밀번호 공백 또는 길이 초과");
            throw new BadRequestException("비밀번호가 유효하지 않습니다.");
        }
        validatePasswordPattern(password);
    }

    private void validatePasswordPattern(final String password) {
        long digitCount = DIGIT_PATTERN.matcher(password).results().count();
        long specialCharCount = SPECIAL_CHAR_PATTERN.matcher(password).results().count();
        long letterCount = LETTER_PATTERN.matcher(password).results().count();

        if (digitCount < MINIMUM_DIGIT_COUNT) {
            log.warn("[VALIDATION-FAIL] 비밀번호 숫자 개수 부족");
        }
        if (specialCharCount < MINIMUM_SPECIAL_CHAR_COUNT) {
            log.warn("[VALIDATION-FAIL] 비밀번호 특수문자 개수 부족");
        }
        if (letterCount < MINIMUM_LETTER_COUNT) {
            log.warn("[VALIDATION-FAIL] 비밀번호 문자 개수 부족");
        }

        if (digitCount < MINIMUM_DIGIT_COUNT
                || specialCharCount < MINIMUM_SPECIAL_CHAR_COUNT
                || letterCount < MINIMUM_LETTER_COUNT) {
            throw new BadRequestException("비밀번호가 유효하지 않습니다.");
        }
    }
}
