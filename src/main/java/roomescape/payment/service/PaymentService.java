package roomescape.payment.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.common.exception.EntityNotFoundException;
import roomescape.common.exception.PaymentBadRequestException;
import roomescape.common.exception.PaymentServerException;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.TossPaymentErrorResponse;
import roomescape.payment.exception.InternalServerErrorCode;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationId;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${toss.payment.secret-key}")
    private String secretKey;

    @Value("${toss.payment.endpoint.confirm}")
    private String confirmEndpoint;

    public PaymentService(
            final PaymentRepository paymentRepository,
            final ReservationRepository reservationRepository,
            final RestClient.Builder builder
    ) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.restClient = builder.build();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void confirm(final PaymentRequest request) {
        String secretKeyWithColon = secretKey + ":";
        byte[] secretKeyBytes = secretKeyWithColon.getBytes(StandardCharsets.UTF_8);

        restClient.post()
                .uri(confirmEndpoint)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(secretKeyBytes))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> handleError(res))
                .toBodilessEntity();
    }

    private void handleError(final ClientHttpResponse res) {
        try (InputStream is = res.getBody()) {
            TossPaymentErrorResponse errorResponse = objectMapper.readValue(is, TossPaymentErrorResponse.class);

            String errorCode = errorResponse.code();
            if (InternalServerErrorCode.contains(errorCode)) {
                throw new PaymentServerException(errorResponse.message());
            }
            throw new PaymentBadRequestException(errorResponse.message());
        } catch (IOException e) {
            throw new RuntimeException("결제 에러 응답 파싱 실패", e);
        }
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
