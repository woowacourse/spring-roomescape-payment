package roomescape.service.reservation.pay;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import roomescape.config.PaymentConfig;
import roomescape.controller.HeaderGenerator;
import roomescape.exception.customexception.api.ApiException;
import roomescape.service.dto.request.PaymentApproveRequest;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.response.PaymentApproveResponse;
import roomescape.service.dto.response.PaymentCancelResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PaymentService {
    // TODO HeadGenerator 받지 않기
    private final PaymentConfig paymentConfig;
    private final IdemPotencyKeyGenerator generator;
    private final RestTemplate restTemplate;

    public PaymentService(
            PaymentConfig paymentConfig,
            IdemPotencyKeyGenerator generator,
            RestTemplate restTemplate
    ) {
        this.paymentConfig = paymentConfig;
        this.generator = generator;
        this.restTemplate = restTemplate;
    }


    public PaymentApproveResponse pay(HeaderGenerator headerGenerator, PaymentApproveRequest paymentApproveRequest) {
        ResponseEntity<PaymentApproveResponse> response = restTemplate.postForEntity(
                paymentConfig.getApproveUrl(),
                new HttpEntity<>(paymentApproveRequest, header()),
                PaymentApproveResponse.class
        );

        validateStatusCode(response.getStatusCode());
        return response.getBody();
    }

    public PaymentCancelResponse cancel(PaymentCancelRequest paymentCancelRequest) {
        HttpHeaders headers = header();
        headers.add("cancelReason", paymentCancelRequest.cancelReason());
        headers.add("Idempotency-Key", generator.generate());

        ResponseEntity<PaymentCancelResponse> response = restTemplate.postForEntity(
                String.format(paymentConfig.getCancelUrl(), paymentCancelRequest.paymentKey()),
                new HttpEntity<>(headers),
                PaymentCancelResponse.class
        );
        validateStatusCode(response.getStatusCode());
        return response.getBody();
    }

    private void validateStatusCode(HttpStatusCode code) {
        if (!code.is2xxSuccessful()) {
            throw new ApiException("결제 요청 과정에서 문제가 발생했습니다.");
        }
    }

    private HttpHeaders header() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(encodeSecretKey());
        return headers;
    }

    private String encodeSecretKey() {
        return Base64.getEncoder()
                .encodeToString((paymentConfig.getSecretKey() + ":")
                        .getBytes(StandardCharsets.UTF_8));
    }
}
