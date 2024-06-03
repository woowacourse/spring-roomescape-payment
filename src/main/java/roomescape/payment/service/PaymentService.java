package roomescape.payment.service;


import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRestClient;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentRestClient restClient;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentRestClient paymentRestClient) {
        this.paymentRepository = paymentRepository;
        this.restClient = paymentRestClient;
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {
        restClient.approvePayment(paymentCreateRequest);
        paymentRepository.save(new Payment(paymentCreateRequest.Reservation(),
                paymentCreateRequest.paymentKey()));
    }

    public void cancelPayment(Long reservationId) {
        Payment payment = paymentRepository.findByReservation_Id(reservationId);
        restClient.cancelPayment(payment.getPaymentKey());
        paymentRepository.deleteByReservation_Id(payment.getReservationId());
    }
}
