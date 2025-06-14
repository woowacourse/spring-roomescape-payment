package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentReservation;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.custom.PaymentBadRequestException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.repository.PaymentReservationRepository;
import roomescape.payment.util.IdempotencyKeyGenerator;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationCommandService;
import roomescape.reservation.service.ReservationQueryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentReservationRepository paymentReservationRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;
    private final TossRestClient restClient;

    @Transactional
    public TossPaymentResponse confirm(final TossPaymentRequest tossPaymentRequest, final long paymentId) {
        log.info("[결제 승인] 시작 - paymentId: {}, amount: {}, orderId: {}", 
                paymentId, tossPaymentRequest.amount(), tossPaymentRequest.orderId());
        
        final Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("[결제 승인] 존재하지 않는 결제 - paymentId: {}", paymentId);
                    return new PaymentBadRequestException("존재하지 않은 결제입니다. paymentId: " + paymentId);
                });
        
        final String idempotencyKey = IdempotencyKeyGenerator.generate();
        log.debug("[결제 승인] Idempotency Key 생성 - paymentId: {}, key: {}", paymentId, idempotencyKey);
        
        try {
            final TossPaymentResponse response = restClient.confirm(tossPaymentRequest, idempotencyKey);
            payment.completePayment();
            log.info("[결제 승인] 완료 - paymentId: {}, status: {}", paymentId, response.status());
            return response;
        } catch (final RuntimeException e) {
            log.error("[결제 승인] 실패 - paymentId: {}, error: {}", paymentId, e.getMessage(), e);
            payment.failPayment();
            
            final PaymentReservation paymentReservation = paymentReservationRepository.findByPaymentId(payment.getId())
                    .orElseThrow(() -> {
                        log.error("[결제 승인] 예약에 대한 결제 내역이 존재하지 않음 - paymentId: {}", payment.getId());
                        return new NotFoundException("예약에 대한 결제 내역이 존재하지 않습니다.");
                    });
            
            final Reservation reservation = paymentReservation.getReservation();
            reservation.cancel();
            log.info("[결제 승인] 예약 취소 - paymentId: {}, reservationId: {}", 
                    paymentId, reservation.getId());
            throw e;
        }
    }

    @Transactional
    public PaymentResponse savePayment(final ReservationPaymentRequest request, final LoginMember loginMember) {
        log.info("[결제 생성] 시작 - memberId: {}, amount: {}, orderId: {}", 
                loginMember.id(), request.amount(), request.orderId());
        
        try {
            final ReservationResponse reservationResponse = reservationCommandService.resisterReservation(request.toReservationRequest(), loginMember);
            final Reservation reservation = reservationQueryService.findById(reservationResponse.id());
            final Payment payment = Payment.builder()
                    .member(reservation.getMember())
                    .paymentStatus(PaymentStatus.PENDING)
                    .paymentKey(request.paymentKey())
                    .orderId(request.orderId())
                    .amount(request.amount())
                    .build();
            paymentRepository.save(payment);

            final Payment savedPayment = paymentRepository.save(payment);
            paymentReservationRepository.save(PaymentReservation.of(savedPayment, reservation));
            
            log.info("[결제 생성] 완료 - paymentId: {}, memberId: {}, amount: {}", 
                    payment.getId(), loginMember.id(), payment.getAmount());
            
            return new PaymentResponse(payment);
        } catch (Exception e) {
            log.error("[결제 생성] 실패 - memberId: {}, error: {}", loginMember.id(), e.getMessage(), e);
            throw e;
        }
    }
}
