package roomescape.payment.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadArgumentRequestException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class PaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(ReservationRepository reservationRepository,
                          MemberRepository memberRepository,
                          PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, Long memberId) {
        Reservation reservation = findReservationByReservationId(request.reservationId());
        validatePaying(reservation, memberId);

        Payment payment = savePayment(request, reservation);
        return PaymentResponse.from(payment);
    }

    private Reservation findReservationByReservationId(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BadArgumentRequestException("해당 예약이 존재하지 않습니다."));
    }

    private void validatePaying(Reservation reservation, Long memberId) {
        if (reservation.isBefore(LocalDateTime.now())) {
            throw new BadArgumentRequestException("결제하기 위해서 해당 예약은 현재 시간 이후여야 합니다.");
        }
        if (reservation.isPaid()) {
            throw new BadArgumentRequestException("해당 예약은 이미 결제 되었습니다.");
        }
        if (reservation.isSameMember(memberId)) {
            throw new BadArgumentRequestException("예약한 회원과 동일한 회원이 결제해야 합니다.");
        }
    }

    private Payment savePayment(PaymentRequest request, Reservation reservation) {
        Payment payment = request.createPayment(reservation.getMember(), reservation.getSchedule());
        return paymentRepository.save(payment);
    }
}
