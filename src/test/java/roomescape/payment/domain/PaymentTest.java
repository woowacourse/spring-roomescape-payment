package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.reservation.domain.MemberReservation;
import roomescape.reservation.domain.ReservationStatus;

@DisplayName("결제 도메인 테스트")
class PaymentTest {

    @DisplayName("결제를 생성한다.")
    @Test
    void create(){
        String paymentKey = "paymentKey";
        PaymentType paymentType = PaymentType.CARD;
        PayAmount payAmount = PayAmount.from(10000L);
        MemberReservation memberReservation = new MemberReservation(
                MemberFixture.getMemberChoco(),
                ReservationFixture.getNextDayReservation(ReservationTimeFixture.get1PM(), ThemeFixture.getTheme1()),
                ReservationStatus.APPROVED
        );

        Payment payment = new Payment(paymentKey, paymentType, payAmount, memberReservation);

        assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(payment.getPaymentType()).isEqualTo(paymentType);
        assertThat(payment.getAmount()).isEqualTo(payAmount);
        assertThat(payment.getMemberReservation()).isEqualTo(memberReservation);
    }
}
