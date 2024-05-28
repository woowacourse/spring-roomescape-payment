package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.exception.BadRequestException;
import roomescape.exception.PaymentFailureException;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentFailure;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.entity.MemberReservation;

import java.util.Base64;
import java.util.Optional;

@Service
public class PaymentService {

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final PaymentRepository paymentRepository;
    @Value("${toss.secret}")
    private String tossSecretKey;

    public PaymentService(ObjectMapper objectMapper, PaymentRepository paymentRepository) {
        this.objectMapper = objectMapper;
        this.paymentRepository = paymentRepository;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }

    public void confirmPayment(PaymentRequest request, MemberReservation memberReservation) {
        String token = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

        PaymentResponse response = getPaymentResponse(request, token)
                .orElseThrow(() -> new PaymentFailureException("결제를 승인하던 중 오류가 발생했습니다."));

        Payment payment = Payment.of(response, memberReservation);
        paymentRepository.save(payment);
    }

    private Optional<PaymentResponse> getPaymentResponse(PaymentRequest request, String token) {
        return Optional.ofNullable(restClient.post()
                .uri("/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + token)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    PaymentFailure paymentFailure = objectMapper.readValue(res.getBody(), PaymentFailure.class);
                    throw new BadRequestException(paymentFailure.message());
                })
                .body(PaymentResponse.class));
    }
}
