package roomescape.service.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentResponseDto;
import roomescape.exception.NotFoundException;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;
import roomescape.infrastructure.TossErrorResponse;
import roomescape.infrastructure.dto.PaymentConfirmResultDto;
import roomescape.repository.JpaPaymentRepository;
import roomescape.repository.JpaReservationRepository;
import roomescape.service.PaymentClient;
import roomescape.service.dto.PaymentConfirmDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentCommandServiceTest {

    @Autowired
    private PaymentCommandService paymentCommandService;

    @MockitoBean
    private PaymentClient paymentClient;

    @MockitoBean
    private JpaPaymentRepository paymentRepository;

    @MockitoBean
    private JpaReservationRepository reservationRepository;

    private Reservation reservation;
    private PaymentConfirmDto requestDto;
    private PaymentConfirmResultDto confirmResultDto;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        requestDto = new PaymentConfirmDto("paymentKey", "orderId", 10000L);
        confirmResultDto = new PaymentConfirmResultDto("paymentKey", "orderId", 10000L);
    }

    @Test
    @DisplayName("결제 확인 시 UNAUTHORIZED_KEY 예외가 발생하면 PaymentConfirmServerException를 던진다")
    void test1() {
        // given
        Long reservationId = 1L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentClient.confirmPayment(requestDto))
                .thenThrow(new PaymentConfirmServerException(
                        new TossErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                "UNAUTHORIZED_KEY",
                                "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.")
                ));

        // when & then
        assertThatThrownBy(() -> paymentCommandService.confirmPayment(reservationId, requestDto))
                .isInstanceOf(PaymentConfirmServerException.class)
                .hasMessage("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.");
    }

    @Test
    @DisplayName("결제 확인 시 ALREADY_PROCESSED_PAYMENT 예외가 발생하면 PaymentConfirmClientException를 던진다")
    void test2() {
        // given
        Long reservationId = 1L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentClient.confirmPayment(requestDto))
                .thenThrow(new PaymentConfirmClientException(
                        new TossErrorResponse(HttpStatus.BAD_REQUEST,
                                "ALREADY_PROCESSED_PAYMENT",
                                "이미 처리된 결제 입니다.")
                ));

        // when & then
        assertThatThrownBy(() -> paymentCommandService.confirmPayment(reservationId, requestDto))
                .isInstanceOf(PaymentConfirmClientException.class)
                .hasMessage("이미 처리된 결제 입니다.");
    }

    @Test
    @DisplayName("결제 확인이 성공하면 Payment를 저장하고 PaymentResponseDto를 반환한다")
    void test3() {
        // given
        Long reservationId = 1L;
        Payment payment = new Payment(requestDto.paymentKey(), requestDto.orderId(), requestDto.amount(), reservation);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentClient.confirmPayment(requestDto)).thenReturn(confirmResultDto);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // when
        PaymentResponseDto result = paymentCommandService.confirmPayment(reservationId, requestDto);

        // then
        assertThat(result.paymentKey()).isEqualTo(payment.getPaymentKey());
        assertThat(result.orderId()).isEqualTo(payment.getOrderId());
        assertThat(result.amount()).isEqualTo(payment.getTotalAmount());
    }

    @Test
    @DisplayName("존재하지 않는 예약에 대한 결제 확인 시 예외가 발생한다")
    void test4() {
        // given
        Long reservationId = 999L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentCommandService.confirmPayment(reservationId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당하는 예약 정보를 찾을 수 없습니다.");
    }
} 