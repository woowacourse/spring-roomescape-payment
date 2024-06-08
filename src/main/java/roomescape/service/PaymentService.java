package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.repository.PaymentRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.request.PaymentSaveDto;
import roomescape.service.response.PaymentDto;

import java.util.Optional;

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

    public PaymentDto save(PaymentSaveDto paymentSaveDto) {
        Long reservationId = paymentSaveDto.reservationId();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("결제 승인 실패: 존재하지 않는 예약입니다. (id: %d)", reservationId)));
        validatePayment(paymentSaveDto, reservation);
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto(paymentSaveDto.paymentKey(), paymentSaveDto.orderId(), paymentSaveDto.amount());
        Payment payment = approveAndSavePayment(paymentApproveDto, reservation);

        return new PaymentDto(payment);
    }

    private Payment approveAndSavePayment(PaymentApproveDto paymentApproveDto, Reservation reservation) {
        PaymentDto paymentDto = paymentManager.approve(paymentApproveDto);
        Payment payment = new Payment(reservation, paymentDto.paymentKey(), paymentDto.orderId(), paymentDto.totalAmount());

        return paymentRepository.save(payment);
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
        Optional<Payment> payment = paymentRepository.findByReservationId(reservationId);
        if (payment.isPresent()) {
            throw new IllegalArgumentException(String.format("이미 결제가 존재하는 예약입니다. (reservationId: %d)", reservationId));
        }
    }

    private void validatePaymentAmount(Reservation reservation, Long amount) {
        boolean isValidAmount = reservation.isPriceEqual(amount);
        if (!isValidAmount) {
            throw new IllegalArgumentException("테마 가격과 결제 금액이 일치하지 않습니다.");
        }
    }
}
