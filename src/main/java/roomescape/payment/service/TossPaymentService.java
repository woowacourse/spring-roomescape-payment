package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.ReservationPaymentRequest;
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
public class TossPaymentService implements PaymentService{

    private final TossRestClient tossRestClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    private final ReservationService reservationService;

    @Transactional
    @Override
    public void saveReservationPayment(final ReservationPaymentRequest request, final LoginMember loginMember) {
        log.debug("ReservationPaymentRequest: {}", request);
        log.debug("LoginMember: {}", loginMember);

        final ReservationResponse reservationResponse = reservationService.registerReservation(
                new CreateRegistrationCommand(loginMember.id(), request.date(), request.timeId(), request.themeId())
        );
        final Reservation reservation = reservationRepository.findById(reservationResponse.id())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다, id:" + reservationResponse.id()));
        log.debug("Reservation ID: {}", reservation.getId());

        final Payment payment = createPayment(request, reservation);
        paymentRepository.save(payment);
    }

    private Payment createPayment(ReservationPaymentRequest request, Reservation reservation) {
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
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );

        try {
            TossPaymentResponse response = tossRestClient.confirm(tossPaymentRequest);
            Payment payment = getPayment(tossPaymentRequest);
            updatePaymentInfoAfterConfirm(payment, response);

        } catch (PaymentTimeoutException e) {
            // 결제 상태 조회 후 결제 취소 API 호출
        } catch (TossPaymentException e) {
            Payment payment = getPayment(tossPaymentRequest);
            payment.updateStatusTo(PaymentStatus.FAILED);
            throw e;
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
    }

    private Payment getPayment(TossPaymentRequest tossPaymentRequest) {
        return paymentRepository.findByPaymentKey(tossPaymentRequest.paymentKey())
                .orElseThrow(() -> new NotFoundException(
                        "Payment를 찾을 수 없습니다, paymentKey: " + tossPaymentRequest.paymentKey()
                ));
    }
}
