package roomescape.service.reservation.pay;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import roomescape.exception.customexception.api.ApiException;
import roomescape.service.dto.request.PaymentApproveRequest;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.response.PaymentApproveResponse;
import roomescape.service.dto.response.PaymentCancelResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PaymentService {
    private final IdemPotencyKeyGenerator generator;
    private final PaymentProperties paymentProperties;
    private final RestTemplate restTemplate;

    public PaymentService(
            IdemPotencyKeyGenerator generator,
            PaymentProperties paymentProperties,
            RestTemplate restTemplate
    ) {
        this.generator = generator;
        this.paymentProperties = paymentProperties;
        this.restTemplate = restTemplate;
    }


    public PaymentApproveResponse pay(PaymentApproveRequest paymentApproveRequest) {
        ResponseEntity<PaymentApproveResponse> response = restTemplate.postForEntity(
                paymentProperties.getApproveUrl(),
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
                String.format(paymentProperties.getCancelUrl(), paymentCancelRequest.paymentKey()),
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
                .encodeToString((paymentProperties.getSecretKey() + ":")
                        .getBytes(StandardCharsets.UTF_8));
    }
}
