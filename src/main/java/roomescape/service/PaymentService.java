package roomescape.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.dto.response.PaymentErrorResponse;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;
import roomescape.exception.custom.PaymentException;
import roomescape.repository.PaymentRepository;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentService(
            PaymentRepository paymentRepository,
            @Qualifier("tossClient") RestClient restClient,
            ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest paymentRequest, Reservation reservation) {
        log.info("결제 승인 시작 - orderId: {}, amount: {}, reservationId: {}",
                paymentRequest.orderId(), paymentRequest.amount(), reservation.getId());

        try {
            String authorizations = getAuthorizationToken();

            ConfirmPaymentResponse body = restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", authorizations)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, ((request, response) -> {
                        PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                                response.getBody().readAllBytes(),
                                PaymentErrorResponse.class);
                        log.error("토스페이먼츠 API 호출 실패 - orderId: {}, statusCode: {}, message: {}",
                                paymentRequest.orderId(), response.getStatusCode(), paymentErrorResponse.message());
                        throw new PaymentException(response.getStatusCode(), paymentErrorResponse.message());
                    }))
                    .toEntity(ConfirmPaymentResponse.class)
                    .getBody();

            log.info("토스페이먼츠 결제 승인 성공 - paymentKey: {}, orderId: {}",
                    body.paymentKey(), body.orderId());

            Payment payment = new Payment(body.orderId(), body.totalAmount(), body.paymentKey(), body.type(),
                    reservation);
            paymentRepository.save(payment);

            log.info("결제 정보 저장 완료 - paymentId: {}, orderId: {}", payment.getId(), payment.getOrderId());

            return body;
        } catch (Exception e) {
            log.error("결제 승인 실패 - orderId: {}, reservationId: {}, error: {}",
                    paymentRequest.orderId(), reservation.getId(), e.getMessage(), e);
            throw e;
        }
    }

    private String getAuthorizationToken() {
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
