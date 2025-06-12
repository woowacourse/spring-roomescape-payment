package roomescape.unit.domain.global;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.reservation.external.toss.TossAuthToken;

class TossAuthTokenTest {

    @Test
    void 토큰을_생성하면_basic이_prefix로_추가된다() {
        // given
        TossAuthToken tossAuthToken = new TossAuthToken("hans");

        // when
        String token = tossAuthToken.generateToken();

        // then
        assertThat(token).containsPattern("basic *");
    }

    @Test
    @DisplayName("secretKey에 콜론(:)을 추가한 후 Base64 UTF-8로 인코딩해서 출력")
    void 토큰은_인코딩된_상태로_조회된다() {
        // given
        TossAuthToken tossAuthToken = new TossAuthToken("hans");

        // when
        String token = tossAuthToken.generateToken();

        // then
        assertThat(token).containsPattern("basic aGFuczo=");

    }
}
