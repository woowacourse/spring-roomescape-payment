package roomescape.application.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static roomescape.fixture.MemberFixture.MEMBER_ARU;
import static roomescape.fixture.ThemeFixture.TEST_THEME;
import static roomescape.fixture.TimeFixture.TEN_AM;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import roomescape.application.ServiceTest;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;
import roomescape.domain.reservation.BookStatus;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationTimeRepository;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.ThemeRepository;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationFixture;

@ServiceTest
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Autowired
    private ReservationPaymentRepository reservationPaymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private PaymentClient paymentClient;

    @Test
    @DisplayName("결제가 정상 처리되면, 결제 정보를 저장한다.")
    void saveOnPurchaseSuccess() {
        Theme theme = themeRepository.save(TEST_THEME.create());
        Reservation reservation = new Reservation(
                memberRepository.save(MEMBER_ARU.create()),
                theme,
                LocalDate.now(),
                reservationTimeRepository.save(TEN_AM.create()),
                LocalDateTime.now(),
                BookStatus.BOOKED
        );
        PaymentRequest request = new PaymentRequest("orderId", theme.getPrice(), "paymentKey");
        given(paymentClient.requestPurchase(any(PaymentRequest.class)))
                .willReturn(new Payment("paymentKey", "orderId", "DONE", theme.getPrice()));

        paymentService.purchase(reservation, request);

        ReservationPayment reservationPayment = reservationPaymentRepository.getByOrderId("orderId");
        assertThat(reservationPayment.getPaymentKey()).isEqualTo("paymentKey");
    }
}
