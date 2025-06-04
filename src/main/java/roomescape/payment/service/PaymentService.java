package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.controller.dto.PaymentVerificationWebRequest;
import roomescape.payment.controller.dto.PaymentVerificationWebResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.dto.CreatePaymentServiceRequest;
import roomescape.payment.service.dto.PaymentConfirmRequest;
import roomescape.payment.service.usecase.PaymentCommandUseCase;
import roomescape.payment.service.usecase.PaymentRestClient;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentCommandUseCase paymentCommandUseCase;
    private final PaymentRestClient paymentRestClient;

    public void confirm(
            final PaymentConfirmRequest paymentConfirmRequest,
            final Long memberId
    ) {
        paymentRestClient.confirm(paymentConfirmRequest, memberId);
    }

    public Payment create(final CreatePaymentServiceRequest createPaymentServiceRequest) {
        return paymentCommandUseCase.create(createPaymentServiceRequest);
    }

    public PaymentVerificationWebResponse createPaymentVerification(
            final PaymentVerificationWebRequest paymentVerificationWebRequest,
            final Long memberId
    ) {
        return paymentCommandUseCase.createPaymentVerification(
                paymentVerificationWebRequest.orderId(),
                paymentVerificationWebRequest.amount(),
                memberId
        );
    }
}
