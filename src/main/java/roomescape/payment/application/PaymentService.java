package roomescape.payment.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import roomescape.global.exception.ViolationException;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.NewPayment;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentCancelInfo;
import roomescape.payment.domain.PaymentCancelResult;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.exception.PaymentServerException;
import roomescape.reservation.event.ReservationDeletedEvent;
import roomescape.reservation.event.ReservationFailedEvent;
import roomescape.reservation.event.ReservationSavedEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PaymentService {
    private static final String CANCEL_REASON_CAUSED_BY_ROLL_BACK = "방탈출 예약의 비정상적 실패";
    private static final String CANCEL_REASON_CAUSED_BY_CUSTOMER = "고객 변심";

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

    @EventListener(ReservationDeletedEvent.class)
    public void delete(ReservationDeletedEvent event) {
        paymentRepository.findByReservationId(event.deletedReservationId()).ifPresent(payment -> {
            paymentRepository.delete(payment);
            PaymentCancelInfo cancelInfo = new PaymentCancelInfo(payment.getPaymentKey(), CANCEL_REASON_CAUSED_BY_CUSTOMER);
            CompletableFuture<PaymentCancelResult> future = paymentClient.cancel(cancelInfo);
            handleCancellationException(future);
        });
    }

    @TransactionalEventListener(value = ReservationFailedEvent.class, phase = TransactionPhase.AFTER_ROLLBACK)
    public void cancelCasedByRollBack(ReservationFailedEvent event) {
        ConfirmedPayment confirmedPayment = event.confirmedPayment();
        PaymentCancelInfo paymentCancelInfo = new PaymentCancelInfo(
                confirmedPayment.paymentKey(), CANCEL_REASON_CAUSED_BY_ROLL_BACK);
        CompletableFuture<PaymentCancelResult> future = paymentClient.cancel(paymentCancelInfo);
        handleCancellationException(future);
    }

    private void handleCancellationException(CompletableFuture<PaymentCancelResult> canceledPayment) {
        try {
            canceledPayment.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ViolationException) {
                throw new ViolationException(cause.getMessage());
            }
            throw new PaymentServerException(cause.getMessage());
        } catch (Exception e) {
            throw new PaymentServerException(e.getMessage());
        }
    }
}
