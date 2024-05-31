package roomescape.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
    @Value("${payment.approve-end-point")
    private static String PAYMENT_APPROVE_ENDPOINT;

    @Value("${payment.cancel-end-point")
    private static String PAYMENT_CANCEL_ENDPOINT;

    @Value("${payment.secret-key}")
    private static String SECRET_KEY;

    private static HttpHeaders HEADER;


    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private static void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(encodeSecretKey());
        HEADER = headers;
    }

    private static String encodeSecretKey() {
        return Base64.getEncoder()
                .encodeToString((SECRET_KEY + ":")
                        .getBytes(StandardCharsets.UTF_8));
    }

    public PaymentApproveResponse pay(HeaderGenerator headerGenerator, PaymentApproveRequest paymentApproveRequest) {
        ResponseEntity<PaymentApproveResponse> response = restTemplate.postForEntity(
                PAYMENT_APPROVE_ENDPOINT,
                new HttpEntity<>(paymentApproveRequest, HEADER),
                PaymentApproveResponse.class
        );

        validateStatusCode(response.getStatusCode());
        return response.getBody();
    }

    public PaymentCancelResponse cancel(PaymentCancelRequest paymentCancelRequest) {
        HEADER.add("cancelReason", paymentCancelRequest.cancelReason());

        ResponseEntity<PaymentCancelResponse> response = restTemplate.postForEntity(
                String.format(PAYMENT_CANCEL_ENDPOINT, paymentCancelRequest.paymentKey()),
                new HttpEntity<>(HEADER),
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
}
