package roomescape.paymenthistory.service;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.paymenthistory.domain.PaymentHistory;
import roomescape.paymenthistory.domain.TossPaymentRestClient;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.dto.PaymentResponse;
import roomescape.paymenthistory.repository.PaymentHistoryRepository;

@Service
public class TossPaymentHistoryService {

    public static final String PAYMENT_HISTORY_NOT_FOUND = "결제 기록이 존재하지 않습니다.";
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
                    paymentCreateRequest.paymentKey(), paymentCreateRequest.amount()));
        } catch (DataIntegrityViolationException e) {
            restClient.cancelPayment(paymentCreateRequest.paymentKey());
            throw e;
        }
    }

    public PaymentResponse findPaymentHistory(long reservationId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByReservation_Id(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(PAYMENT_HISTORY_NOT_FOUND));

        return new PaymentResponse(paymentHistory.getPaymentKey(), paymentHistory.getAmount().getAmount());
    }

    public void cancelPayment(Long reservationId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByReservation_Id(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(PAYMENT_HISTORY_NOT_FOUND));
        restClient.cancelPayment(paymentHistory.getPaymentKey());
        paymentHistoryRepository.deleteByReservation_Id(paymentHistory.getReservationId());
    }
}
