package roomescape.service;

import static roomescape.exception.RoomescapeErrorCode.INTERNAL_SERVER_ERROR;
import static roomescape.exception.RoomescapeErrorCode.PAYMENT_NOT_FOUND;
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
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationWithPaymentRequest;
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

    public PaymentResponse createPayment(final ReservationWithPaymentRequest request) {
        Payment payment = new Payment(request.paymentKey(), request.orderId(), request.amount());
        Payment saved = paymentRepository.save(payment);
        return PaymentResponse.from(saved);
    }

    public void confirm(final PaymentConfirmRequest request) {
        final Payment payment = getPaymentByPaymentKey(request.paymentKey());
        final Reservation reservation = getReservationByPayment(payment);
        try {
            PaymentConfirmResponse response = paymentClient.confirm(request);
            validateConfirmResponse(payment, response);
            payment.confirm();
            reservation.pay(payment);
            paymentRepository.save(payment);
            reservationRepository.save(reservation);
        } catch (ResourceAccessException e) {
            throw new RoomescapeException(REQUEST_TIMEOUT);
        }
    }

    private void validateConfirmResponse(final Payment payment, final PaymentConfirmResponse response) {
        if (Objects.equals(payment.getPaymentKey(), response.paymentKey())
                && Objects.equals(payment.getOrderId(), response.orderId())
                && Objects.equals(payment.getAmount(), response.totalAmount())
        ) {
            return;
        }
        throw new RoomescapeException(INTERNAL_SERVER_ERROR);
    }

    private Payment getPaymentByPaymentKey(final String paymentKey) {
        return paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new RoomescapeException(PAYMENT_NOT_FOUND));
    }

    private Reservation getReservationByPayment(final Payment payment) {
        return reservationRepository.findByPayment(payment)
                .orElseThrow(() -> new RoomescapeException(RESERVATION_NOT_FOUND));
    }
}
