package roomescape.application.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.dto.PaymentClientRequest;
import roomescape.application.payment.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.payment.PaymentStatus;
import roomescape.exception.payment.PaymentException;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient,
                          PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createPayment(PaymentRequest request) {
        Payment payment = new Payment(request.orderId(), request.paymentKey(), request.amount());
        return paymentRepository.save(payment);
    }

    public Payment purchase(PaymentRequest request) {
        Payment payment = paymentRepository.getByOrderId(request.orderId());
        if (payment.isPurchased()) {
            throw new PaymentException("이미 결제된 항목입니다.");
        }
        paymentClient.requestPurchase(
                new PaymentClientRequest(payment.getOrderId(), payment.getAmount(), request.paymentKey())
        );
        if (paymentRepository.updateStatus(request.orderId(), request.paymentKey(), PaymentStatus.SUCCESS)) {
            return payment.purchase();
        }
        throw new PaymentException("결제에 실패했습니다.");
    }
}
