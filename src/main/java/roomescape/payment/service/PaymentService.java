package roomescape.payment.service;


import java.util.Optional;
import org.springframework.stereotype.Service;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRestClient;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.payment.dto.RestClientPaymentApproveResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

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
        RestClientPaymentApproveResponse restClientPaymentApproveResponse =
                restClient.approvePayment(paymentCreateRequest.createRestClientPaymentApproveRequest());
        paymentRepository.save(restClientPaymentApproveResponse.createPayment(paymentCreateRequest.reservation()));
    }

    public void cancelPayment(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RoomEscapeException(
                        "결제되지 않은 예약이라 취소가 불가능합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
        restClient.cancelPayment(payment.getPaymentKey());
        paymentRepository.deleteByReservation_Id(payment.getReservationId());
    }

    public Optional<Payment> findPaymentByReservation(Reservation reservation) {
        return paymentRepository.findByReservationId(reservation.getId());
    }
}
