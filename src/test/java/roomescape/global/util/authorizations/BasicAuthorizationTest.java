package roomescape.global.util.authorizations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.global.util.Authorization;

@DisplayName("Basic Authorization 단위 테스트")
class BasicAuthorizationTest {

    @DisplayName("Basic Authorization의 헤더 반환에 성공한다.")
    @Test
    void getHeader() {
        //given
        Authorization authorization = new BasicAuthorization();
        String str = "My Header";
        String expected = "Basic My Header";

        //when
        String header = authorization.getHeader(str);

        //then
        assertAll(
                () -> assertThat(header).isNotNull(),
                () -> assertThat(header).isEqualTo(expected)
        );
    }
}
