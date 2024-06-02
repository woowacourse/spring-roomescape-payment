package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.dto.PaymentRequest;
import roomescape.exception.RoomescapeException;
import roomescape.infra.PaymentClient;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@SpringBootTest
@Sql("/data/payment.sql")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockBean
    private PaymentClient paymentClient;

    @Test
    @DisplayName("결제가 정상 처리되면, 결제 정보를 저장한다.")
    void savePayment() {
        // given
        Reservation reservation = reservationRepository.findById(2L).orElseThrow();

        PaymentRequest request = new PaymentRequest(reservation.getId(), "paymentKey", "WTESTzzzzz", 1000L);
        given(paymentClient.requestPaymentApproval(any(PaymentRequest.class)))
                .willReturn(new Payment(null, "paymentKey", "WTESTzzzzz", 1000L));

        // when & then
        assertThatCode(() -> paymentService.payReservation(request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("결제가 실패하면, 예약의 결제 정보를 갱신하지 않는다.")
    void failPaymentThenReservationPaymentInfoNotUpdated() {
        // given
        Reservation reservation = reservationRepository.findById(2L).orElseThrow();

        given(paymentClient.requestPaymentApproval(any(PaymentRequest.class)))
                .willThrow(new RuntimeException());

        // when
        Reservation foundReservation = reservationRepository.findById(reservation.getId()).get();

        // then
        assertAll(
                () -> assertThat(foundReservation.getStatus()).isEqualTo(ReservationStatus.RESERVED_UNPAID),
                () -> assertThat(foundReservation.getPayment()).isNull()
        );
    }

    @Test
    @DisplayName("이미 결제된 예약은 결제를 시도할 수 없다.")
    void failPaymentWhenReservationStatusIsPaid() {
        // given
        Reservation reservation = reservationRepository.findById(1L).orElseThrow();
        Payment payment = paymentRepository.findById(1L).orElseThrow();
        PaymentRequest paymentRequest = new PaymentRequest(reservation.getId(), payment.getPaymentKey(), payment.getOrderId(),
                payment.getAmount());

        // when & then
        assertThatThrownBy(() -> paymentService.payReservation(paymentRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("이미 결제가 되었습니다.");
    }

    @Test
    @DisplayName("대기중인 예약은 결제를 시도할 수 없다.")
    void failPaymentWhenReservationStatusIsPending() {
        // given
        Reservation reservation = reservationRepository.findById(3L).orElseThrow();
        PaymentRequest paymentRequest = new PaymentRequest(reservation.getId(), "paymentKey", "WTEST000001", 1000L);

        // when & then
        assertThatThrownBy(() -> paymentService.payReservation(paymentRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("대기중인 예약은 결제가 불가능 합니다.");
    }
}
