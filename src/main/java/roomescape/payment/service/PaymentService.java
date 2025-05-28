package roomescape.payment.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.TossPayment;
import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.domain.dto.PaymentResponseDto;
import roomescape.payment.exception.InvalidPaymentException;

import java.nio.charset.StandardCharsets;

@Service
public class PaymentService {

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponseDto approve(PaymentRequestDto paymentRequestDto) {
        TossPayment payment = restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentRequestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    HttpStatusCode statusCode = res.getStatusCode();
                    HttpStatus httpStatus = HttpStatus.valueOf(statusCode.value());
                    throw new InvalidPaymentException(body, httpStatus);
                })
                .body(TossPayment.class);

        return convertPaymentResponseDto(payment);
    }

    private static PaymentResponseDto convertPaymentResponseDto(TossPayment payment) {
        return PaymentResponseDto.of(payment);
    }
}
