package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentCancelRequest;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.dto.CreateRegistrationCommand;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {

    private final TossRestClient tossRestClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    private final ReservationService reservationService;

    @Transactional
    @Override
    public void registerReservation(final ReservationPaymentRequest request, final LoginMember loginMember) {
        log.debug("ReservationPaymentRequest: {}", request);
        log.debug("LoginMember: {}", loginMember);

        final ReservationResponse reservationResponse = reservationService.registerReservation(
                new CreateRegistrationCommand(loginMember.id(), request.date(), request.timeId(), request.themeId())
        );
        final Reservation reservation = getReservationById(reservationResponse.id());
        log.debug("Reservation ID: {}", reservation.getId());

        final Payment payment = createPendingPayment(request, reservation);
        paymentRepository.save(payment);
    }

    private Payment createPendingPayment(ReservationPaymentRequest request, Reservation reservation) {
        return Payment.builder()
                .paymentKey(request.paymentKey())
                .orderId(request.orderId())
                .amount(request.amount())
                .reservation(reservation)
                .member(reservation.getMember())
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Transactional
    @Override
    public void confirmPayment(final ReservationPaymentRequest request) {
        TossPaymentRequest tossRequest =  new TossPaymentRequest(request.paymentKey(), request.orderId(), request.amount());
        Payment payment = getPaymentByKey(tossRequest);
        confirmPaymentInternal(payment, tossRequest);
    }

    @Transactional
    public void completePaymentForReservation(final Long reservationId,
                                              final PaymentRequest request,
                                              final LoginMember loginMember) {
        validateReservationOwnership(reservationId, loginMember);

        Payment payment = getPaymentByReservationId(reservationId);
        payment.assignPaymentInformation(request.paymentKey(), request.orderId(), request.amount());

        TossPaymentRequest tossRequest =  new TossPaymentRequest(request.paymentKey(), request.orderId(), request.amount());
        confirmPaymentInternal(payment, tossRequest);
    }

    private void validateReservationOwnership(Long reservationId, LoginMember loginMember) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation.isOwnedBy(loginMember.id())) {
            return;
        }
        throw new TossPaymentException(HttpStatus.BAD_REQUEST, "본인의 예약만 결제할 수 있습니다.", false);
    }

    private void confirmPaymentInternal(Payment payment, TossPaymentRequest tossRequest) {
        try {
            TossPaymentResponse response = tossRestClient.confirm(tossRequest);
            validateCanConfirmStatus(payment);
            updatePaymentInfoAfterConfirm(payment, response);
        } catch (PaymentTimeoutException e) {

            // 결제 상태 조회 후 결제 취소 API 호출 (필요 시 구현)
        } catch (TossPaymentException e) {
            payment.updateStatusTo(PaymentStatus.FAILED);
            throw e;
        }
    }

    private void validateCanConfirmStatus(Payment payment) {
        if(payment.canConfirmPaymentStatus()) {
            return;
        }
        throw new TossPaymentException(HttpStatus.BAD_REQUEST, "결제가 가능한 상태가 아닙니다, 현재 상태: " + payment.getStatus().getDescription(), false);
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
    }

    @Override
    public void saveNotPaidPayment(Reservation reservation) {
        Payment payment = Payment.builder()
                .member(reservation.getMember())
                .reservation(reservation)
                .status(PaymentStatus.NOT_PAID)
                .build();
        paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public void cancelPayment(Long reservationId) {
        validateReservationExists(reservationId);
        Payment payment = getPaymentByReservationId(reservationId);

        TossPaymentCancelRequest cancelRequest = new TossPaymentCancelRequest("모종의 이유");
        TossPaymentResponse cancelResponse = tossRestClient.cancel(payment.getPaymentKey(), cancelRequest);
        validateCancelSuccess(cancelResponse);
        payment.updateStatusTo(PaymentStatus.REFUNDED);
    }

    private void validateReservationExists(Long reservationId) {
        if(reservationRepository.existsById(reservationId)) {
            return;
        }
        throw new NotFoundException("존재하지 않는 예약입니다, id: " + reservationId);
    }

    private void validateCancelSuccess(TossPaymentResponse cancelResponse) {
        if(cancelResponse.status().equals("CANCELED")){
            return;
        }
        throw new TossPaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 취소 실패", true);
    }

    private Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다, id:" + id));
    }

    private Payment getPaymentByKey(TossPaymentRequest tossPaymentRequest) {
        return paymentRepository.findByPaymentKey(tossPaymentRequest.paymentKey())
                .orElseThrow(() -> new NotFoundException(
                        "Payment를 찾을 수 없습니다, paymentKey: " + tossPaymentRequest.paymentKey()
                ));
    }

    private Payment getPaymentByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다, id: " + reservationId));
    }
}
