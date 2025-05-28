package roomescape.reservation.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import roomescape.common.exception.EntityNotFoundException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationId;
import roomescape.reservation.payment.domain.Payment;
import roomescape.reservation.payment.dto.request.PaymentRequest;
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

        ResponseEntity<Payment> response = restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(secretKeyBytes))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(Payment.class);
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
