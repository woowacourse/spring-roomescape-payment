package roomescape.service;

import static roomescape.exception.RoomescapeExceptionCode.INTERNAL_SERVER_ERROR;
import static roomescape.exception.RoomescapeExceptionCode.RESERVATION_NOT_FOUND;

import org.springframework.stereotype.Service;

import roomescape.component.TossPaymentClient;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.exception.RoomescapeException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
public class PaymentService {

    private final TossPaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(final TossPaymentClient paymentClient,
            final PaymentRepository paymentRepository,
            final ReservationRepository reservationRepository
    ) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public void confirm(final PaymentConfirmRequest paymentConfirmRequest, final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomescapeException(RESERVATION_NOT_FOUND));
        final Payment payment = paymentConfirmRequest.toPayment(reservation);
        PaymentConfirmResponse response = paymentClient.confirm(paymentConfirmRequest);
        requireCorrectAttributes(payment, response);
        paymentRepository.save(payment);
    }

    private void requireCorrectAttributes(final Payment payment, final PaymentConfirmResponse response) {
        if (!isCorrectAttributes(payment, response)) {
            throw new RoomescapeException(INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isCorrectAttributes(final Payment payment, final PaymentConfirmResponse response) {
        return payment.samePaymentKey(response.paymentKey())
                && payment.sameOrderId(response.orderId())
                && payment.sameAmount(response.totalAmount());
    }
}
