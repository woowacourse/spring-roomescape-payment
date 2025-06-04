package roomescape.payment.service.usecase;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.common.exception.PaymentException;
import roomescape.payment.domain.PaymentVerification;
import roomescape.payment.service.PaymentErrorHandler;
import roomescape.payment.service.dto.PaymentConfirmRequest;
import roomescape.payment.service.dto.PaymentConfirmResponse;

@Service
public class PaymentRestClient {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final RestClient restClient;
    private final PaymentQueryUseCase paymentQueryUseCase;

    public PaymentRestClient(
            final Builder restClientBuilder,
            final PaymentQueryUseCase paymentQueryUseCase
    ) {
        this.paymentQueryUseCase = paymentQueryUseCase;

        restClient = restClientBuilder
                .defaultStatusHandler(new PaymentErrorHandler())
                .defaultHeader("Authorization", getAuthorization())
                .defaultHeader("Content-Type", "application/json")
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }

    public PaymentConfirmResponse confirm(
            final PaymentConfirmRequest request,
            final Long memberId
    ) {
        validatePaymentConfirm(request, memberId);

        return restClient.post()
                .uri("/confirm")
                .body(request)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }

    private void validatePaymentConfirm(
            final PaymentConfirmRequest request,
            final Long memberId
    ) {
        final PaymentVerification paymentVerification = paymentQueryUseCase.getPaymentVerificationByOrderId(
                request.orderId()
        );

        if (!paymentVerification.isSamePayment(
                request.orderId(),
                request.amount(),
                memberId)
        ) {
            throw new PaymentException(HttpStatus.BAD_REQUEST, "잘못된 결제 승인 요청입니다.");
        }
    }

    private String getAuthorization() {
        return "Basic " + encodeToBase64((SECRET_KEY + ":"));
    }

    private String encodeToBase64(final String value) {
        final Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(value.getBytes(StandardCharsets.UTF_8)));
    }
}
