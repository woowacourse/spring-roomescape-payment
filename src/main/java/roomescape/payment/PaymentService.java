package roomescape.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.reservation.dto.CreateReservationRequest;
import roomescape.domain.PaymentInfo;
import roomescape.domain.Reservation;
import roomescape.payment.dto.CreatePaymentRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.repository.PaymentRepository;
import roomescape.service.exception.PaymentInfoNotFoundException;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void savePayment(final CreateReservationRequest request, final Reservation savedReservation) {
        //TODO 파라미터 애매함
        final PaymentConfirmResponse paymentConfirmResponse = paymentClient.postPayment(
                new CreatePaymentRequest(request.paymentKey(), request.orderId(), request.amount()));
        final PaymentInfo payment = paymentConfirmResponse.toPayment(savedReservation);
        paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayment(final long id) {
        try {
            final PaymentInfo paymentInfo = paymentRepository.fetchByReservationId(id);
            paymentRepository.delete(paymentInfo);
        } catch (final PaymentInfoNotFoundException ignore) {
        }
    }
}
