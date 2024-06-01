package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

@SpringBootTest
class PaymentRestClientTest {

    @Autowired
    PaymentRestClient paymentRestClient;

    @DisplayName("결제 시간이 만료된 경우 커스텀 예외를 발생한다.")
    @Test
    void approvePaymentTest_whenInvalidSecretKeyd() {
        PaymentCreateRequest paymentCreateRequest = new PaymentCreateRequest(
                "tgen_20240528211", "MC40MTMwMTk0ODU0ODU4", 1000, new Reservation(1L, MemberFixture.MEMBER_BRI,
                LocalDate.now().plusDays(1), TimeFixture.TIME_1, ThemeFixture.THEME_1, ReservationStatus.RESERVED));

        assertThatThrownBy(
                () -> paymentRestClient.approvePayment(paymentCreateRequest))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }
}
