package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class ThemeTest {

    @Nested
    @DisplayName("테마를 생성할 때 검증을 수행한다.")
    class validate {

        @DisplayName("비어있는 이름으로는 테마를 생성할 수 없다")
        @ParameterizedTest
        @NullAndEmptySource
        void cannotCreateBecauseNullName(String name) {
            // when & then
            assertThatThrownBy(() -> Theme.createWithoutId(name, "설명", "섬네일"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 이름으로 테마를 생성할 수 없습니다.");
        }

        @Test
        @DisplayName("최대길이를 초과한 이름으로는 테마를 생성할 수 없다")
        void cannotCreateBecauseTooLongName() {
            // given
            String tooLongName = "1".repeat(51);

            // when & then
            assertThatThrownBy(() -> Theme.createWithoutId(tooLongName, "설명", "섬네일"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("최대길이를 초과한 이름으로 테마를 생성할 수 없습니다.");
        }

        @DisplayName("비어있는 설명으로는 테마를 생성할 수 없다")
        @ParameterizedTest
        @NullAndEmptySource
        void cannotCreateBecauseNullDescription(String description) {
            // when & then
            assertThatThrownBy(() -> Theme.createWithoutId("테마", description, "섬네일"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 설명으로 테마를 생성할 수 없습니다.");
        }

        @DisplayName("비어있는 썸네일로는 테마를 생성할 수 없다")
        @ParameterizedTest
        @NullAndEmptySource
        void cannotCreateBecauseNullThumbnail(String thumbnail) {
            // when & then
            assertThatThrownBy(() -> Theme.createWithoutId("테마", "설명", thumbnail))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 썸네일로 테마를 생성할 수 없습니다.");
        }

        @Test
        @DisplayName("최대길이를 초과한 썸네일로는 테마를 생성할 수 없다")
        void cannotCreateBecauseTooLongThumbnail() {
            // given
            String tooLongThumbnail = "1".repeat(601);

            // when & then
            assertThatThrownBy(() -> Theme.createWithoutId("테마", "설명", tooLongThumbnail))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("최대길이를 초과한 썸네일로 테마를 생성할 수 없습니다.");
        }
    }
}
