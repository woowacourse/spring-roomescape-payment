package roomescape.domain.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.infrastructure.error.exception.MemberException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class EmailTest {

    @Test
    void 이메일_정상_생성() {
        // given
        final String emailValue = "bello@email.com";

        // when
        final Email email = new Email(emailValue);

        // then
        assertThat(email.value())
                .isEqualTo("bello@email.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "bello@email",
            "bello@.com",
            "@email.com",
            "bello@com",
            "belloemail.com",
            "bello@.com.",
            "."
    })
    void 이메일_형식_검증(final String invalidEmailValue) {
        // when
        // then
        assertThatCode(() -> new Email(invalidEmailValue))
                .isInstanceOf(MemberException.class)
                .hasMessage("이메일 형식이 아닙니다.");
    }
}
