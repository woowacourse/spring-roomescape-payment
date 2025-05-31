package roomescape.domain.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import roomescape.exception.BusinessRuleViolationException;

class ThemeTest {

    @Test
    @DisplayName("이름이 10자를 초과하는 경우 예외를 던진다.")
    void validateName_WhenTooLong() {
        // given
        var tooLongName = "이름이10자를초과하는경우";
        var description = "우테코 레벨1을 탈출하는 내용입니다.";
        var thumbnail = "https://image.url";

        // when & then
        assertThatThrownBy(() -> Theme.register(tooLongName, description, thumbnail))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("이름은 10자를 넘길 수 없습니다.");
    }

    @Test
    @DisplayName("설명이 50자를 초과하는 경우 예외를 던진다.")
    void validateDescription_WhenTooLong() {
        // given
        var name = "레벨1 탈출";
        var tooLongDescription = "a".repeat(51);
        var thumbnail = "https://image.url";

        // when & then
        assertThatThrownBy(() -> Theme.register(name, tooLongDescription, thumbnail))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("설명은 50자를 넘길 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름은 null이거나 공백일 수 없다.")
    void validateName_WhenNullOrEmpty(final String name) {
        // given
        var description = "우테코 레벨1을 탈출하는 내용입니다.";
        var thumbnail = "https://image.url";

        // when & then
        assertThatThrownBy(() -> Theme.register(name, description, thumbnail))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("테마 이름은 null이거나 공백일 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름은 null이거나 공백일 수 없다.")
    void validateDescription_WhenNullOrEmpty(final String description) {
        // given
        var name = "레벨1 탈출";
        var thumbnail = "https://image.url";

        // when & then
        assertThatThrownBy(() -> Theme.register(name, description, thumbnail))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("테마 설명은 null이거나 공백일 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("썸네일은 null이거나 공백일 수 없다.")
    void validateThumbnail_WhenNullOrEmpty(final String thumbnail) {
        // given
        var name = "레벨1 탈출";
        var description = "우테코 레벨1을 탈출하는 내용입니다.";

        // when & then
        assertThatThrownBy(() -> Theme.register(name, description, thumbnail))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("썸네일은 null이거나 공백일 수 없습니다.");
    }

    @Test
    @DisplayName("테마를 정상적으로 생성한다.")
    void register() {
        // given
        var name = "레벨1 탈출";
        var description = "우테코 레벨1을 탈출하는 내용입니다.";
        var thumbnail = "https://image.url";

        // when
        Theme theme = Theme.register(name, description, thumbnail);

        // then
        assertAll(
                () -> assertThat(theme).isNotNull(),
                () -> assertThat(theme.getName()).isEqualTo(name),
                () -> assertThat(theme.getDescription()).isEqualTo(description),
                () -> assertThat(theme.getThumbnail()).isEqualTo(thumbnail)
        );
    }
}
