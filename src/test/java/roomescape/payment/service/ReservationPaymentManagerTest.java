package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static roomescape.util.Fixture.LOGIN_MEMBER_KAKI;
import static roomescape.util.Fixture.TODAY;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.auth.dto.LoginMember;
import roomescape.config.DatabaseCleaner;
import roomescape.config.IntegrationTest;
import roomescape.exception.PaymentFailException;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSaveRequest;
import roomescape.reservation.repository.ReservationRepository;

class ReservationPaymentManagerTest extends IntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ReservationPaymentManager manager;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("결제에 성공하면 SUCCESS 상태의 예약이 저장된다.")
    @Test
    void paySuccessThenCreateSuccessReservation() {
        saveMemberAsKaki();
        saveReservationTimeAsTen();
        saveThemeAsHorror();

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        ReservationSaveRequest reservationSaveRequest
                = new ReservationSaveRequest(loginMember.id(), TODAY, 1L, 1L, "testKey", "testId", 1000);
        PaymentRequest paymentRequest = PaymentRequest.from(reservationSaveRequest);
        Payment payment = paymentRequest.toPaymentStatusReady();

        PaymentResponse paymentResponse = new PaymentResponse("testKey", new BigDecimal("1000"));
        doReturn(payment).when(paymentService).createPayment(paymentRequest, 1L);
        doReturn(paymentResponse).when(paymentService).confirm(payment);

        ReservationResponse reservationResponse = manager.saveReservationByUser(reservationSaveRequest, loginMember);
        Reservation reservation = reservationRepository.findById(reservationResponse.id())
                .orElseThrow(() -> new NoSuchElementException("해당되는 예약이 없습니다."));

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.SUCCESS);
    }

    @DisplayName("결제에 실패하면 예약이 저장되지 않는다.")
    @Test
    void payFailThenDoNotSaveReservation() {
        saveMemberAsKaki();
        saveReservationTimeAsTen();
        saveThemeAsHorror();

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        ReservationSaveRequest reservationSaveRequest
                = new ReservationSaveRequest(loginMember.id(), TODAY, 1L, 1L, "testKey", "testId", 1000);
        PaymentRequest paymentRequest = PaymentRequest.from(reservationSaveRequest);
        Payment payment = paymentRequest.toPaymentStatusReady();

        doReturn(payment).when(paymentService).createPayment(paymentRequest, 1L);
        doThrow(PaymentFailException.class).when(paymentService).confirm(payment);

        try {
            manager.saveReservationByUser(reservationSaveRequest, loginMember);
        } catch (Exception exception) {
        }

        assertThat(reservationRepository.findById(1L)).isEmpty();
    }
}
