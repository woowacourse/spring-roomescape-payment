package roomescape.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.facade.dto.ReservationWithPaymentResponseDto;
import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.dto.ReservationRequestDto;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.reservation.domain.dto.ReservationWithPaymentDto;
import roomescape.reservation.service.ReservationService;
import roomescape.user.domain.User;

@Service
public class ReservationPaymentFacade {

    private final ReservationService reservationService;
    private final PaymentService tossPaymentService;

    public ReservationPaymentFacade(ReservationService reservationService, PaymentService tossPaymentService) {
        this.reservationService = reservationService;
        this.tossPaymentService = tossPaymentService;
    }

    @Transactional
    public ReservationWithPaymentResponseDto addWithPayment(ReservationWithPaymentDto requestDto, User user) {
        ReservationRequestDto reservationRequestDto = requestDto.toReservationRequestDto();
        ReservationResponseDto reservationResponseDto = reservationService.add(reservationRequestDto, user);

        PaymentRequestDto paymentRequestDto = requestDto.toPaymentRequestDto();
        PaymentResponseDto approvedPaymentInfo = tossPaymentService.approve(paymentRequestDto, reservationResponseDto.toReservation());
        return ReservationWithPaymentResponseDto.of(reservationResponseDto, approvedPaymentInfo);
    }
}
