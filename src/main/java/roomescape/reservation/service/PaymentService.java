package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.client.PaymentClient;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;

@Service
@Transactional(readOnly = true)
public class PaymentService {
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void purchase(PaymentRequest request, long reservationId) {
        paymentClient.confirm(getAuthorizations(), request);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 예약은 결제할 수 없습니다."));
        Payment payment = new Payment(request.amount(), request.paymentKey(), reservation);
        paymentRepository.save(payment);
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

}
