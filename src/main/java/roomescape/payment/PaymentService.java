package roomescape.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.reservation.dto.CreateReservationRequest;
import roomescape.domain.PaymentInfo;
import roomescape.domain.Reservation;
import roomescape.payment.dto.CancelPaymentRequest;
import roomescape.payment.dto.CreatePaymentRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.repository.PaymentRepository;
import roomescape.service.exception.PaymentInfoNotFoundException;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
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
    public void deletePayment(final long reservationId) {
        try {
            final PaymentInfo paymentInfo = paymentRepository.fetchByReservationId(reservationId);
            paymentClient.cancelPayment(new CancelPaymentRequest(paymentInfo.getPaymentKey(), "단순변심"));
            paymentRepository.delete(paymentInfo);
        } catch (final PaymentInfoNotFoundException ignore) {
            log.warn("결제 정보가 존제하지 않습니다.");
        }
    }
}
