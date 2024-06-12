package roomescape.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.dto.PaymentConfirmResponse;
import roomescape.domain.payment.dto.SavePaymentCredentialRequest;
import roomescape.domain.payment.exception.PaymentCredentialMissMatchException;
import roomescape.domain.payment.model.PaymentCredential;
import roomescape.domain.payment.model.PaymentHistory;
import roomescape.domain.payment.pg.PaymentGateway;
import roomescape.domain.payment.repository.PaymentCredentialRepository;
import roomescape.domain.payment.repository.PaymentHistoryRepository;
import roomescape.domain.reservation.dto.PaymentHistoryDto;
import roomescape.domain.reservation.model.Reservation;

import java.util.NoSuchElementException;

@Service
public class PaymentService {

    private final PaymentCredentialRepository paymentCredentialRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentGateway tossPaymentGateway;

    public PaymentService(
            final PaymentCredentialRepository paymentCredentialRepository,
            final PaymentHistoryRepository paymentHistoryRepository,
            final PaymentGateway tossPaymentGateway
    ) {
        this.paymentCredentialRepository = paymentCredentialRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.tossPaymentGateway = tossPaymentGateway;
    }

    public void saveCredential(final SavePaymentCredentialRequest request) {
        final PaymentCredential paymentCredential = request.toModel();
        paymentCredentialRepository.save(paymentCredential);
    }

    @Transactional
    public void submitPayment(
            final String orderId,
            final Long amount,
            final String paymentKey,
            final Reservation reservation
    ) {
        matchPaymentCredential(orderId, amount);

        final PaymentConfirmResponse confirmResponse = tossPaymentGateway.confirm(orderId, amount, paymentKey);
        final PaymentHistory paymentHistory = confirmResponse.toModel(reservation);

        paymentHistoryRepository.save(paymentHistory);

        paymentCredentialRepository.deleteAllByOrderIdAndAmount(orderId, amount);
    }

    private void matchPaymentCredential(final String orderId, final Long amount) {
        final boolean isMatch = paymentCredentialRepository.existsByOrderIdAndAmount(orderId, amount);
        if (!isMatch) {
            throw new PaymentCredentialMissMatchException("결제 정보가 유효하지 않습니다.");
        }
    }

    @Transactional
    public void cancelPayment(final Reservation reservation) {
        final PaymentHistory paymentHistory = getPaymentHistory(reservation);
        paymentHistory.cancelPayment();
    }

    private PaymentHistory getPaymentHistory(final Reservation reservation) {
        final PaymentHistory paymentHistory = paymentHistoryRepository.findByReservation(reservation)
                .orElseThrow(() -> new NoSuchElementException("해당 예약에 대한 결제 정보가 없습니다. " + reservation));
        return paymentHistory;
    }

    public void savePaymentHistory(final PaymentHistoryDto request) {
        paymentHistoryRepository.save(request.toModel());
    }
}
