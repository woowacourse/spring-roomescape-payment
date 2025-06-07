package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.exception.InvalidPaymentException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.external.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TossPaymentServiceTest {

    private final TossRestClient mockRestClient = mock(TossRestClient.class);
    private final PaymentRepository mockPaymentRepository = mock(PaymentRepository.class);
    private final ReservationRepository mockReservationRepository = mock(ReservationRepository.class);

    @Test
    void 결제_승인_요청시_200_OK() {
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000);

        PaymentResponseDto dummyPaymentDto = new PaymentResponseDto("paymentKey", "orderId", 1000);
        mockReservationRepository.save(getReservation());

        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenReturn(dummyPaymentDto);

        TossPaymentService tossPaymentService = new TossPaymentService(mockRestClient, mockPaymentRepository);

        Assertions.assertThatCode(
                () -> tossPaymentService.approve(dto, getReservation())
        ).doesNotThrowAnyException();
    }

    private Reservation getReservation() {
        Reservation dummyReservation = new Reservation(
                1L,
                LocalDate.of(2025, 5, 24),
                ReservationStatus.BOOKED,
                new ReservationTime(1L, LocalTime.of(10, 0)),
                new Theme(2L, "테스트 테마", "공포의 밀실", "무서운 방입니다."),
                new User(1L, Role.ROLE_MEMBER, "유저1","user@email.com", "user")
        );
        return dummyReservation;
    }

    @Test
    void 결제_승인_요청시_400에러가_발생하면_InvalidPaymentException_발생() {
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000);

        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenThrow(new InvalidPaymentException(HttpStatus.BAD_REQUEST));

        TossPaymentService tossPaymentService = new TossPaymentService(mockRestClient, mockPaymentRepository);

        Assertions.assertThatThrownBy(
                () -> tossPaymentService.approve(dto, getReservation())
        ).isInstanceOf(InvalidPaymentException.class);
    }

    @Test
    void 결제_승인_요청시_500에러가_발생하면_PaymentServerException_발생() {
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000);

        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenThrow(new PaymentServerException(HttpStatus.INTERNAL_SERVER_ERROR));

        TossPaymentService tossPaymentService = new TossPaymentService(mockRestClient, mockPaymentRepository);

        Assertions.assertThatThrownBy(
                () -> tossPaymentService.approve(dto, getReservation())
        ).isInstanceOf(PaymentServerException.class);
    }
}
