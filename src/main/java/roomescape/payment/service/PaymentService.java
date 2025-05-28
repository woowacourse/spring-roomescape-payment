package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.payment.Payment;
import roomescape.payment.PaymentRequestDto;
import roomescape.payment.domain.dto.PaymentResponseDto;
import roomescape.payment.exception.InvalidPaymentException;

@Service
public class PaymentService {

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponseDto approve(PaymentRequestDto paymentRequestDto) {
        Payment payment = restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentRequestDto)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new InvalidPaymentException();
                })
                .body(Payment.class);

        return convertPaymentResponseDto(payment);
    }

    private static PaymentResponseDto convertPaymentResponseDto(Payment payment) {
        return PaymentResponseDto.of(payment);
    }
}
