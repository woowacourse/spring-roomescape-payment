package roomescape.reservation.payment.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.common.exception.EntityNotFoundException;
import roomescape.common.exception.PaymentBadRequestException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationId;
import roomescape.reservation.payment.domain.Payment;
import roomescape.reservation.payment.dto.request.PaymentRequest;
import roomescape.reservation.payment.dto.response.TossPaymentErrorResponse;
import roomescape.reservation.payment.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final RestClient restClient;

    @Value("${toss.payment.secret-key}")
    private String secretKey;

    public PaymentService(
            final PaymentRepository paymentRepository,
            final ReservationRepository reservationRepository,
            final RestClient restClient
    ) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.restClient = restClient;
    }

    public void confirm(final PaymentRequest request) {
        String secretKeyWithColon = secretKey + ":";
        byte[] secretKeyBytes = secretKeyWithColon.getBytes(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(secretKeyBytes))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    try (InputStream is = res.getBody()) {
                        TossPaymentErrorResponse errorResponse = objectMapper.readValue(is, TossPaymentErrorResponse.class);
                        throw new PaymentBadRequestException(errorResponse.message());
                    } catch (IOException e) {
                        throw new RuntimeException("결제 에러 응답 파싱 실패", e);
                    }
                })
                .body(Payment.class);
    }

    public void savePayment(final Long reservationId, final PaymentRequest paymentRequest) {
        Reservation reservation = reservationRepository.findById(new ReservationId(reservationId))
                .orElseThrow(() -> new EntityNotFoundException("해당 예약이 존재하지 않습니다."));

        paymentRepository.save(new Payment(
                paymentRequest.paymentKey(),
                paymentRequest.orderId(),
                paymentRequest.amount(),
                reservation
                ));
    }
}
