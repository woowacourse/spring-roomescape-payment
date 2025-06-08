package roomescape.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.client.PaymentClient;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(final PaymentRepository paymentRepository, final PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Payment confirmAndSavePayment(final PaymentsConfirmRequest request) {
        log.info("결제 확인 요청 시작 - paymentKey: {}", request.paymentKey());
        final PaymentsConfirmResponse paymentsConfirmResponse = paymentClient.confirmPayments(request);
        log.info("결제 확인 완료 - paymentKey: {}, amount: {}", paymentsConfirmResponse.paymentKey(),
                paymentsConfirmResponse.totalAmount());

        final Payment payment = new Payment(paymentsConfirmResponse.paymentKey(),
                paymentsConfirmResponse.totalAmount());
        final Payment savedPayment = paymentRepository.save(payment);
        log.info("결제 정보 저장 완료 - paymentId: {}", savedPayment.getId());
        return savedPayment;
    }
}
