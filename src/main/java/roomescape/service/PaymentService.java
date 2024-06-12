package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.repository.PaymentRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.request.PaymentCancelDto;
import roomescape.service.request.PaymentSaveDto;
import roomescape.service.response.PaymentDto;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentManager paymentManager;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentManager paymentManager, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentManager = paymentManager;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public PaymentDto save(PaymentSaveDto paymentSaveDto) {
        Long reservationId = paymentSaveDto.reservationId();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("결제 승인 실패: 존재하지 않는 예약입니다. (id: %d)", reservationId)));
        validatePayment(paymentSaveDto, reservation);
        PaymentDto paymentDto = approvePayment(paymentSaveDto);
        Payment payment = new Payment(reservation, paymentDto.paymentKey(), paymentDto.orderId());
        Payment savedPayment = savePayment(payment);

        return PaymentDto.from(savedPayment);
    }

    private PaymentDto approvePayment(PaymentSaveDto paymentSaveDto) {
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto(paymentSaveDto.paymentKey(), paymentSaveDto.orderId(), paymentSaveDto.amount());
        return paymentManager.approve(paymentApproveDto);
    }

    private Payment savePayment(Payment payment) {
        try {
            return paymentRepository.save(payment);
        } catch (Exception e) {
            paymentManager.cancel(payment.getPaymentKey(), new PaymentCancelDto("결제 정보 저장 중 오류가 발생했습니다."));
            throw e;
        }
    }

    private void validatePayment(PaymentSaveDto paymentSaveDto, Reservation reservation) {
        validateReservationMember(paymentSaveDto.memberId(), reservation);
        validatePaymentNotExistBy(reservation.getId());
        validatePaymentAmount(reservation, paymentSaveDto.amount());
    }

    private void validateReservationMember(Long paidMemberId, Reservation reservation) {
        Long reservationMemberId = reservation.getMember().getId();
        if (!reservationMemberId.equals(paidMemberId)) {
            throw new IllegalArgumentException(String.format("예약에 대한 결제 권한이 없는 사용자입니다. (memberId: %d", paidMemberId));
        }
    }

    private void validatePaymentNotExistBy(Long reservationId) {
        paymentRepository.findByReservationId(reservationId)
                .ifPresent((payment -> {
                    throw new IllegalArgumentException(String.format("이미 결제가 존재하는 예약입니다. (reservationId: %d)", reservationId));
                }));
    }

    private void validatePaymentAmount(Reservation reservation, Long amount) {
        boolean isValidAmount = reservation.isPriceEqual(amount);
        if (!isValidAmount) {
            throw new IllegalArgumentException("테마 가격과 결제 금액이 일치하지 않습니다.");
        }
    }
}
