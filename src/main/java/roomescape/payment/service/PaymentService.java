package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.custom.PaymentBadRequestException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.util.IdempotencyKeyGenerator;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationCommandService;
import roomescape.reservation.service.ReservationQueryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;
    private final TossRestClient restClient;

    @Transactional
    public TossPaymentResponse confirm(final TossPaymentRequest tossPaymentRequest, final long id) {
        final Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentBadRequestException("존재하지 않은 결제입니다."));
        final String idempotencyKey = IdempotencyKeyGenerator.generate();
        try {
            final TossPaymentResponse response = restClient.confirm(tossPaymentRequest, idempotencyKey);
            payment.completePayment();
            return response;
        } catch (RuntimeException e) {
            payment.failPayment();
            final Reservation reservation = payment.getReservation();
            reservationQueryService.findById(reservation.getId());
            throw e;
        }
    }

    @Transactional
    public PaymentResponse savePayment(final ReservationPaymentRequest request, final LoginMember loginMember) {
        log.debug("ReservationPaymentRequest: {}", request);
        log.debug("LoginMember: {}", loginMember);

        final ReservationResponse reservationResponse = reservationCommandService.resisterReservation(request.toReservationRequest(), loginMember);
        final Reservation reservation = reservationQueryService.findById(reservationResponse.id());
        log.debug("Reservation ID: {}", reservation.getId());

        final Payment payment = Payment.builder()
                .paymentKey(request.paymentKey())
                .orderId(request.orderId())
                .amount(request.amount())
                .paymentStatus(PaymentStatus.PENDING)
                .reservation(reservation)
                .member(reservation.getMember())
                .build();
        return new PaymentResponse(paymentRepository.save(payment));
    }
}
