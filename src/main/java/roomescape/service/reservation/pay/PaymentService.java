package roomescape.service.reservation.pay;

import org.springframework.http.*;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import roomescape.domain.repository.PaymentRepository;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.exception.customexception.api.ApiException;
import roomescape.service.dto.request.PaymentApproveRequest;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.response.PaymentApproveResponse;
import roomescape.service.dto.response.PaymentCancelResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PaymentService {
    private static final int RETRY_ATTEMPT = 2;

    private final IdemPotencyKeyGenerator generator;
    private final PaymentProperties paymentProperties;
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            IdemPotencyKeyGenerator generator,
            PaymentProperties paymentProperties,
            RestTemplate restTemplate,
            PaymentRepository paymentRepository
    ) {
        this.generator = generator;
        this.paymentProperties = paymentProperties;
        this.restTemplate = restTemplate;
        this.paymentRepository = paymentRepository;
    }

    public PaymentApproveResponse pay(PaymentApproveRequest paymentApproveRequest, Reservation reservation) {
        Payment payment = new Payment(paymentApproveRequest.paymentKey(), paymentApproveRequest.amount(), reservation);
        paymentRepository.save(payment);
        return approvePay(paymentApproveRequest);
    }

    @Retryable(maxAttempts = RETRY_ATTEMPT)
    private PaymentApproveResponse approvePay(PaymentApproveRequest paymentApproveRequest) {
        ResponseEntity<PaymentApproveResponse> response = restTemplate.postForEntity(
                paymentProperties.getApproveUrl(),
                new HttpEntity<>(paymentApproveRequest, header()),
                PaymentApproveResponse.class
        );
        validateStatusCode(response.getStatusCode());
        return response.getBody();
    }

    @Retryable(maxAttempts = RETRY_ATTEMPT)
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
