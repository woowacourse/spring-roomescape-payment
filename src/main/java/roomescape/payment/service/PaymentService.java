package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.custom.EntityNotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.gateway.PaymentGateway;
import roomescape.payment.gateway.PaymentGatewayResolver;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationId;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentGatewayResolver resolver;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository,
                          PaymentGatewayResolver resolver) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.resolver = resolver;
    }

    @Transactional
    public void create(final Long reservationId, final PaymentRequest paymentRequest) {
        Reservation reservation = reservationRepository.findById(new ReservationId(reservationId))
                .orElseThrow(() -> new EntityNotFoundException("해당 예약이 존재하지 않습니다."));

        paymentRepository.save(new Payment(
                paymentRequest.paymentKey(),
                paymentRequest.orderId(),
                paymentRequest.amount(),
                reservation
        ));
    }

    public void confirm(final PaymentRequest paymentRequest) {
        PaymentGateway paymentGateway = resolver.resolve(paymentRequest.method());
        paymentGateway.confirm(paymentRequest);
    }
}
