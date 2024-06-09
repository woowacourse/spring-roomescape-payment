package roomescape.service;

import static roomescape.exception.RoomescapeErrorCode.INTERNAL_SERVER_ERROR;
import static roomescape.exception.RoomescapeErrorCode.REQUEST_TIMEOUT;
import static roomescape.exception.RoomescapeErrorCode.RESERVATION_NOT_FOUND;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

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

    public PaymentService(
            final TossPaymentClient paymentClient,
            final PaymentRepository paymentRepository,
            final ReservationRepository reservationRepository
    ) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public void confirm(final PaymentConfirmRequest request) {
        final Reservation reservation = getReservation(request.reservationId());
        final Payment payment = request.toPayment(reservation);
        try {
            PaymentConfirmResponse response = paymentClient.confirm(request);
            validateResponse(payment, response);
        } catch (ResourceAccessException e) {
            throw new RoomescapeException(REQUEST_TIMEOUT);
        }
        paymentRepository.save(payment);
    }

    private void validateResponse(final Payment payment, final PaymentConfirmResponse response) {
        if (Objects.equals(payment.getPaymentKey(), response.paymentKey())
                && Objects.equals(payment.getOrderId(), response.orderId())
                && Objects.equals(payment.getAmount(), response.totalAmount())
        ) {
            return;
        }
        throw new RoomescapeException(INTERNAL_SERVER_ERROR);
    }

    private Reservation getReservation(final long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomescapeException(RESERVATION_NOT_FOUND));
    }
}
