package roomescape.paymenthistory.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.config.properties.PaymentProperties;
import roomescape.config.properties.TossPaymentProperties;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.exception.PaymentException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

@SpringBootTest
@EnableConfigurationProperties(TossPaymentProperties.class)
class TossPaymentRestClientTest {

    private final TossPaymentRestClient tossPaymentRestClient;

    @Autowired
    public TossPaymentRestClientTest(PaymentProperties paymentProperties) {
        this.tossPaymentRestClient = new TossPaymentRestClient(paymentProperties);
    }

    @Disabled
    @DisplayName("예외가 발생하는 경우 예외를 적절히 변환하여 반환한다.")
    @Test
    void approvePaymentTest_whenOverPaymentTime() {
        PaymentCreateRequest paymentCreateRequest = new PaymentCreateRequest(
                "tgen_20240528211", "MC40MTMwMTk0ODU0ODU4", 1000, new Reservation(1L, MemberFixture.MEMBER_BRI,
                LocalDate.now().plusDays(1), TimeFixture.TIME_1, ThemeFixture.THEME_1, ReservationStatus.RESERVED));

        assertThatThrownBy(
                () -> tossPaymentRestClient.approvePayment(paymentCreateRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }
}
