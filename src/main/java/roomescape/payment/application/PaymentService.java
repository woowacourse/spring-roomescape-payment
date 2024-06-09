package roomescape.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.custom.BadRequestException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.infra.PaymentClient;

import java.util.Objects;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment purchase(PaymentRequest paymentRequest, Long amount) {
        PaymentResponse paymentResponse = paymentClient.confirm(paymentRequest);

        if (!Objects.equals(paymentResponse.totalAmount(), amount)) {
            throw new BadRequestException("결제 금액이 잘못되었습니다.");
        }
        Payment payment = new Payment(
                paymentResponse.orderId(),
                paymentResponse.paymentKey(),
                paymentResponse.totalAmount(),
                paymentResponse.method(),
                paymentResponse.requestedAt(),
                paymentResponse.approvedAt()
        );
        return paymentRepository.save(payment);
    }
}
