package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.controller.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;

@Service
@Transactional
public class PaymentService {

    private final String widgetSecretKey;
    private final String authorizations;
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            @Value("${toss.secret-key}") String widgetSecretKey,
            PaymentClient paymentClient,
            PaymentRepository paymentRepository
    ) {
        this.widgetSecretKey = widgetSecretKey;
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        authorizations = getAuthorizations();
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public void pay(ReservationCreateRequest reservationCreateRequest, Reservation reservation) {
        paymentClient.paymentReservation(authorizations, PaymentRequest.toRequest(reservationCreateRequest))
                .getBody();

        paymentRepository.save(
                new Payment(reservationCreateRequest.paymentKey(), reservationCreateRequest.amount(), reservation));
    }

    public Payment findByReservation(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 결제 내역이 없습니다."));
    }

    public void deleteByReservationId(Long reservationId) {
        paymentRepository.deleteByReservationId(reservationId);
    }
}
