package roomescape.paymenthistory.service;


import org.springframework.stereotype.Service;
import roomescape.paymenthistory.domain.PaymentHistory;
import roomescape.paymenthistory.domain.PaymentRestClient;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.repository.PaymentHistoryRepository;

@Service
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentRestClient restClient;

    public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository,
                                 PaymentRestClient paymentRestClient) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.restClient = paymentRestClient;
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {
        restClient.approvePayment(paymentCreateRequest);
        paymentHistoryRepository.save(new PaymentHistory(paymentCreateRequest.Reservation(),
                paymentCreateRequest.paymentKey()));
    }

    public void cancelPayment(Long reservationId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByReservation_Id(reservationId);
        restClient.cancelPayment(paymentHistory.getPaymentKey());
        paymentHistoryRepository.deleteByReservation_Id(paymentHistory.getReservationId());
    }
}
