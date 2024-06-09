package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.USER_ID;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.infrastructure.tosspayments.TossPaymentsClient;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.ServiceBaseTest;

class ReservationRegisterServiceTest extends ServiceBaseTest {

    @Autowired
    ReservationRegisterService reservationRegisterService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @MockBean
    TossPaymentsClient tossPaymentsClient;

    @Test
    void 예약_등록() {
        // given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(7), 1L, 1L, USER_ID);

        PaymentRequest paymentRequest = new PaymentRequest(
                "paymentKey", "orderId", BigDecimal.valueOf(1000), "paymentType");

        PaymentResponse paymentResponse = new PaymentResponse(
                paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount());
        Mockito.when(tossPaymentsClient.requestPayment(paymentRequest)).thenReturn(paymentResponse);

        // when
        ReservationResponse response = reservationRegisterService.registerReservation(reservationRequest, paymentRequest);

        // then
        Reservation reservation = reservationRepository.findByIdOrThrow(response.id());
        Payment payment = paymentRepository.findByReservationIdOrThrow(reservation.getId());

        assertAll(
                () -> assertThat(reservation.getDate()).isEqualTo(reservationRequest.date()),
                () -> assertThat(reservation.getTime().getId()).isEqualTo(reservationRequest.timeId()),
                () -> assertThat(reservation.getMember().getId()).isEqualTo(reservationRequest.memberId()),
                () -> assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED),
                () -> assertThat(payment.getPaymentKey()).isEqualTo(paymentRequest.paymentKey()),
                () -> assertThat(payment.getOrderId()).isEqualTo(paymentRequest.orderId()),
                () -> assertThat(payment.getAmount()).isEqualTo(paymentRequest.amount())
        );
    }

    @Test
    void 대기_예약_등록() {
        // given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(2), 1L, 1L, 5L);

        // when
        ReservationResponse response = reservationRegisterService.registerWaitingReservation(reservationRequest);

        // then
        Reservation reservation = reservationRepository.findByIdOrThrow(response.id());
        assertThat(reservation.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void 관리자_계정은_결제_없이_예약_등록() {
        // given
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(2), 1L, 1L, 5L);

        // when
        ReservationResponse response = reservationRegisterService.registerWaitingReservation(reservationRequest);

        // then
        Reservation reservation = reservationRepository.findByIdOrThrow(response.id());
        assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED);
    }

    @Test
    void 결제_대기_예약은_결제를_진행하여_예약_완료_상태로_변경() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                "paymentKey", "orderId", BigDecimal.valueOf(1000), "paymentType");

        PaymentResponse paymentResponse = new PaymentResponse(
                paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount());
        Mockito.when(tossPaymentsClient.requestPayment(paymentRequest)).thenReturn(paymentResponse);

        // when
        ReservationResponse response = reservationRegisterService.requestPaymentByPaymentPending(3L, paymentRequest);

        // then
        Reservation reservation = reservationRepository.findByIdOrThrow(response.id());
        Payment payment = paymentRepository.findByReservationIdOrThrow(reservation.getId());
        assertAll(
                () -> assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED),
                () -> assertThat(payment.getPaymentKey()).isEqualTo(paymentRequest.paymentKey()),
                () -> assertThat(payment.getOrderId()).isEqualTo(paymentRequest.orderId()),
                () -> assertThat(payment.getAmount()).isEqualTo(paymentRequest.amount())
        );
    }
}
