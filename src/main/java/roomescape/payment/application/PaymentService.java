package roomescape.payment.application;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.application.dto.PrePaymentRequest;
import roomescape.payment.domain.PrePayment;
import roomescape.payment.domain.PrePaymentRepository;

@Service
@AllArgsConstructor
public class PaymentService {
    private final PrePaymentRepository prePaymentRepository;

    public void savePrePayment(PrePaymentRequest request) {
        prePaymentRepository.save(new PrePayment(request.orderId(), request.amount()));
    }
}
