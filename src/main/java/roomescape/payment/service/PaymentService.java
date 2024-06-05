package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.PaymentClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void confirmPayment(Reservation reservation, PaymentConfirmRequest paymentConfirmRequest) {
        PaymentConfirmResponse paymentConfirmResponse = paymentClient.requestConfirmPayment(paymentConfirmRequest);
        paymentRepository.save(paymentConfirmResponse.toEntity((reservation)));
    }
}
