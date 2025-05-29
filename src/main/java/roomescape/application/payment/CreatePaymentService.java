package roomescape.application.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.dto.CreatePaymentCommand;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.repository.PaymentRepository;

@Service
@Transactional
public class CreatePaymentService {

    private final PaymentRepository paymentRepository;

    public CreatePaymentService(final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Long register(final CreatePaymentCommand createPaymentCommand) {
        final Payment payment = new Payment(createPaymentCommand.orderId(), createPaymentCommand.amount());
        final Payment savedPayment = paymentRepository.save(payment);
        return savedPayment.getId();
    }
}
