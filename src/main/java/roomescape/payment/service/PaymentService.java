package roomescape.payment.service;

import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.reservation.domain.Reservation;

public interface PaymentService {

    PaymentResponseDto approve(PaymentRequestDto request, Reservation reservation);

    void cancelPaymentByReservation(Reservation reservation);
}
