package roomescape.payment.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentSaveResponse;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@Transactional
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(TossPaymentClient tossPaymentClient, PaymentRepository paymentRepository) {
        this.tossPaymentClient = tossPaymentClient;
        this.paymentRepository = paymentRepository;
    }

    public PaymentSaveResponse payForReservation(@Valid PaymentRequest paymentRequest, Reservation reservation) {
        TossPaymentResponse tossPaymentResponse = tossPaymentClient.requestPayment(paymentRequest);
        Payment savedPayment = paymentRepository.save(tossPaymentResponse.from(reservation));

        return PaymentSaveResponse.toResponse(savedPayment);
    }
}
