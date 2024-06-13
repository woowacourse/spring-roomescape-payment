package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.dto.PaymentRequest;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.repository.PaymentRepository;
import roomescape.service.dto.response.PaymentCancelRequest;
import roomescape.service.dto.response.PaymentResponse;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public void pay(Reservation reservation, PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentClient.pay(paymentRequest);

        try {
            Payment payment = paymentResponse.toPayment(reservation);
            paymentRepository.save(payment);

            throw new RuntimeException();
        } catch (RuntimeException runtimeException) {
            cancel(paymentResponse);
            throw new RuntimeException("예상치 못한 서버 에러가 발생했습니다.");
        }
    }

    private void cancel(PaymentResponse paymentResponse) {
        PaymentCancelRequest paymentCancelRequest = PaymentCancelRequest.from(paymentResponse);
        paymentClient.cancel(paymentCancelRequest);
    }
}
