package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossConfirmationService {

    private final ReservationPaymentCreator reservationPaymentCreator;
    private final NotPaidPaymentProcessor notPaidPaymentProcessor;

    private final TossRestClient tossRestClient;
    private final PaymentRepository paymentRepository;

    public void reserveAndPay(ReservationPaymentRequest reservationPaymentRequest, LoginMember loginMember) {
        reservationPaymentCreator.saveReservationAndPayment(reservationPaymentRequest, loginMember);
        confirmPayment(reservationPaymentRequest.toPaymentRequest());

        log.info("예약+결제 완료 - memberId={}, paymentKey={}",
                loginMember.id(), reservationPaymentRequest.paymentKey());
    }

    private void confirmPayment(final PaymentRequest request) {
        TossPaymentRequest tossPaymentRequest = toTossPaymentRequestFrom(request);
        Payment payment = getPaymentByKey(tossPaymentRequest);
        validateCanConfirmStatus(payment);
        confirmToTossWithFallBack(payment, tossPaymentRequest);
        log.info("결제 승인 완료 - paymentId = {}", payment.getId());
    }

    private void validateCanConfirmStatus(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new TossPaymentException(HttpStatus.BAD_REQUEST,
                    "결제를 승인할 수 있는 상태가 아닙니다, 현재 상태: " + payment.getStatus().getDescription(), false);
        }
    }

    private void updatePaymentInfoAfterConfirm(Payment payment, TossPaymentResponse response) {
        payment.updateStatusTo(PaymentStatus.COMPLETED);
        payment.updateConfirmed(
                response.method(),
                response.cardNumber(),
                response.cardApprovedNo(),
                response.easyPayProvider(),
                response.receiptUrl()
        );
        paymentRepository.save(payment);
    }

    public void proceedPaymentForNotPaidReservation(final Long reservationId,
                                                    final PaymentRequest request,
                                                    final LoginMember loginMember) {
        Payment payment = notPaidPaymentProcessor.prepareNotPaidToPending(reservationId, request, loginMember);
        TossPaymentRequest tossPaymentRequest = toTossPaymentRequestFrom(request);
        confirmToTossWithFallBack(payment, tossPaymentRequest);
    }

    private void confirmToTossWithFallBack(Payment payment, TossPaymentRequest tossPaymentRequest) {
        try {
            TossPaymentResponse response = tossRestClient.confirm(tossPaymentRequest);
            updatePaymentInfoAfterConfirm(payment, response);

            log.info("결제 승인 성공 - paymentKey={}, orderId={}",
                    payment.getPaymentKey(), response.orderId());
        } catch (PaymentTimeoutException e) {
            log.warn("결제 승인 타임아웃 - paymentKey={}, orderId={}",
                    tossPaymentRequest.paymentKey(), tossPaymentRequest.orderId());
            payment.updateStatusTo(PaymentStatus.FAILED);
            // 결제 상태 조회 후 결제 취소 API 호출 (필요 시 구현)
            throw e;
        } catch (TossPaymentException e) {
            log.warn("결제 승인 실패 - paymentKey={}, message={}",
                    tossPaymentRequest.paymentKey(), e.getMessage());
            payment.updateStatusTo(PaymentStatus.FAILED);
            throw e;
        } finally {
            paymentRepository.save(payment);
        }
    }

    private Payment getPaymentByKey(TossPaymentRequest tossPaymentRequest) {
        return paymentRepository.findByPaymentKey(tossPaymentRequest.paymentKey())
                .orElseThrow(() -> new NotFoundException(
                        "Payment를 찾을 수 없습니다, paymentKey: " + tossPaymentRequest.paymentKey()
                ));
    }

    private TossPaymentRequest toTossPaymentRequestFrom(PaymentRequest paymentRequest) {
        return new TossPaymentRequest(paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount());
    }
}
