package roomescape.payment.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.NewPayment;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentRepository;
import roomescape.reservation.event.ReservationSavedEvent;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public ConfirmedPayment confirm(NewPayment newPayment) {
        return paymentClient.confirm(newPayment);
    }

    @EventListener(ReservationSavedEvent.class)
    public void create(ReservationSavedEvent event) {
        ConfirmedPayment confirmedPayment = event.confirmedPayment();
        Payment payment = confirmedPayment.toModel(event.reservation());
        paymentRepository.save(payment);
    }
}
