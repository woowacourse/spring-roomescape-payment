package roomescape.payment.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import roomescape.client.payment.TossPaymentClient;
import roomescape.client.payment.dto.PaymentConfirmationFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmationToTossDto;
import roomescape.exception.PaymentException;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.dto.ReservationRequest;

import java.util.Objects;

@Service
public class PaymentService {

    private static final String PAYMENT_SAVE_CONDITION_STATUS = "DONE";

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    public PaymentService(PaymentRepository paymentRepository, TossPaymentClient tossPaymentClient) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentClient = tossPaymentClient;
    }

    public void sendConfirmRequestAndSavePayment(ReservationRequest reservationRequest, Reservation reservation) {
        PaymentConfirmationToTossDto paymentConfirmationToTossDto = PaymentConfirmationToTossDto.from(reservationRequest);
        PaymentConfirmationFromTossDto paymentConfirmationFromTossDto = tossPaymentClient.sendPaymentConfirm(paymentConfirmationToTossDto);
        validateConfirmationStatus(paymentConfirmationFromTossDto);
        Payment payment = paymentConfirmationFromTossDto.toPayment(reservation);

        paymentRepository.save(payment);
    }

    private void validateConfirmationStatus(PaymentConfirmationFromTossDto paymentConfirmationFromTossDto) {
        if (!Objects.equals(paymentConfirmationFromTossDto.status(), PAYMENT_SAVE_CONDITION_STATUS)) {
            throw new PaymentException(HttpStatus.BAD_REQUEST, "결제가 승인되지 않았습니다.");
        }
    }
}
