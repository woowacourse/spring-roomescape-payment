package roomescape.reservation.payment.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.common.exception.EntityNotFoundException;
import roomescape.common.exception.PaymentBadRequestException;
import roomescape.common.exception.PaymentServerException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationId;
import roomescape.reservation.payment.domain.Payment;
import roomescape.reservation.payment.dto.request.PaymentRequest;
import roomescape.reservation.payment.dto.response.TossPaymentErrorResponse;
import roomescape.reservation.payment.exception.InternalServerErrorCode;
import roomescape.reservation.payment.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class PaymentService {

    private static final String PAYMENTS_CONFIRM_ENDPOINT = "https://api.tosspayments.com/v1/payments/confirm";

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final RestClient restClient;

    @Value("${toss.payment.secret-key}")
    private String secretKey;

    public PaymentService(
            final PaymentRepository paymentRepository,
            final ReservationRepository reservationRepository,
            final RestClient.Builder builder
    ) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.restClient = builder.baseUrl(PAYMENTS_CONFIRM_ENDPOINT)
                .build();
    }

    public void confirm(final PaymentRequest request) {
        String secretKeyWithColon = secretKey + ":";
        byte[] secretKeyBytes = secretKeyWithColon.getBytes(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        restClient.post()
                .uri(PAYMENTS_CONFIRM_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(secretKeyBytes))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    try (InputStream is = res.getBody()) {
                        TossPaymentErrorResponse errorResponse =
                                objectMapper.readValue(is, TossPaymentErrorResponse.class);
                        String errorCode = errorResponse.code();
                        if (InternalServerErrorCode.contains(errorCode)) {
                            throw new PaymentServerException(errorResponse.message());
                        }
                        throw new PaymentBadRequestException(errorResponse.message());
                    } catch (IOException e) {
                        throw new RuntimeException("결제 에러 응답 파싱 실패", e);
                    }
                })
                .toBodilessEntity();
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
