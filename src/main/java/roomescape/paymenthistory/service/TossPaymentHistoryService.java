package roomescape.paymenthistory.service;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.paymenthistory.domain.PaymentHistory;
import roomescape.paymenthistory.domain.TossPaymentRestClient;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.repository.PaymentHistoryRepository;

@Service
public class TossPaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final TossPaymentRestClient restClient;

    public TossPaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository,
                                     TossPaymentRestClient restClient) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.restClient = restClient;
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {
        try {
            restClient.approvePayment(paymentCreateRequest);
            paymentHistoryRepository.save(new PaymentHistory(paymentCreateRequest.Reservation(),
                    paymentCreateRequest.paymentKey()));
        } catch (DataIntegrityViolationException e) {
            restClient.cancelPayment(paymentCreateRequest.paymentKey());
            throw e;
        }
    }

    public void cancelPayment(Long reservationId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByReservation_Id(reservationId);
        restClient.cancelPayment(paymentHistory.getPaymentKey());
        paymentHistoryRepository.deleteByReservation_Id(paymentHistory.getReservationId());
    }
}
