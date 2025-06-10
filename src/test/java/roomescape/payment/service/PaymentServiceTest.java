package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.member.domain.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.exception.PaymentRequestException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservationTime.domain.ReservationTimeRepository;
import roomescape.theme.domain.ThemeRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PaymentServiceTest {

    private static final String PAYMENT_KEY = "tgen_20240513184816ZSAZ9";
    private static final String ORDER_ID = "MC4wNDYzMzA0OTc2MDgy";
    private static final int AMOUNT = 1000;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("예약 ID로 결제 정보를 조회할 수 있다.")
    @Test
    void can_find_payment_by_reservation_id() {
        Long reservationId = 1L;

        Payment foundPayment = paymentService.findByReservationId(reservationId);

        assertThat(foundPayment.getPaymentKey()).isEqualTo("abcd");
        assertThat(foundPayment.getOrderId()).isEqualTo("주문1");
        assertThat(foundPayment.getAmount()).isEqualTo(1000);
    }

    @DisplayName("존재하지 않는 예약 ID로 결제 정보를 조회하면 예외가 발생한다.")
    @Test
    void cannot_find_payment_by_non_existent_reservation_id() {
        Long nonExistentReservationId = 999L;

        Assertions.assertThatThrownBy(() -> paymentService.findByReservationId(nonExistentReservationId))
            .isInstanceOf(PaymentRequestException.class)
            .hasMessage("결제 정보를 찾을 수 없습니다.");
    }
}