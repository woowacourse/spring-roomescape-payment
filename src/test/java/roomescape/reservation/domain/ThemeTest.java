package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;

@DisplayName("테마 도메인 테스트")
class ThemeTest {
    @DisplayName("동일한 id는 같은 테마다.")
    @Test
    void equals() {
        //given
        long id1 = 1;
        String name1 = "name1";
        String description1 = "description1";
        String thumbnail1 = "thumbnail1";
        long price1 = 15000L;

        String name2 = "name2";
        String description2 = "description2";
        String thumbnail2 = "thumbnail2";
        long price2 = 20000L;

        //when
        Theme theme1 = new Theme(id1, name1, description1, thumbnail1, price1);
        Theme theme2 = new Theme(id1, name2, description2, thumbnail2, price2);

        //then
        assertThat(theme1).isEqualTo(theme2);
    }

    @DisplayName("문자열 필드에 빈칸을 허용하지 않는다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void invalidStings(String value) {
        //given
        long id = 1;
        String name = "name1";
        String description = "description1";
        String thumbnail = "thumbnail1";
        long price = 15000L;

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> new Theme(id, value, description, thumbnail, price))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(ErrorType.MISSING_REQUIRED_VALUE_ERROR.getMessage()),
                () -> assertThatThrownBy(() -> new Theme(id, name, value, thumbnail, price))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(ErrorType.MISSING_REQUIRED_VALUE_ERROR.getMessage()),
                () -> assertThatThrownBy(() -> new Theme(id, name, description, value, price))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(ErrorType.MISSING_REQUIRED_VALUE_ERROR.getMessage())
        );
    }
}
