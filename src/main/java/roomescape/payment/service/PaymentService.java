package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.dto.SavePaymentCredentialRequest;
import roomescape.payment.model.PaymentCredential;
import roomescape.payment.repository.PaymentCredentialRepository;

@Service
public class PaymentService {

    private final PaymentCredentialRepository paymentCredentialRepository;

    public PaymentService(final PaymentCredentialRepository paymentCredentialRepository) {
        this.paymentCredentialRepository = paymentCredentialRepository;
    }

    public void saveCredential(final SavePaymentCredentialRequest request) {
        final PaymentCredential paymentCredential = request.toPaymentCredential();
        paymentCredentialRepository.save(paymentCredential);
    }
}
