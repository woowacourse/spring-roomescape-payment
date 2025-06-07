package roomescape.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.common.exception.NotFoundException;
import roomescape.infrastructure.db.TossPaymentJpaRepository;
import roomescape.model.PaymentTargetType;
import roomescape.model.ReservationTicket;
import roomescape.model.TossPayment;

@Repository
@RequiredArgsConstructor
public class TossPaymentRepositoryImpl implements TossPaymentRepository {

    private final TossPaymentJpaRepository tossPaymentJpaRepository;

    @Override
    public TossPayment save(TossPayment tossPayment) {
        return tossPaymentJpaRepository.save(tossPayment);
    }

    @Override
    public TossPayment findForReservationTicket(ReservationTicket reservationTicket) {
        return tossPaymentJpaRepository.findByTargetIdAndPaymentTargetType(reservationTicket.getId(),
                        PaymentTargetType.RESERVATION_TICKET)
                .orElseThrow(() -> new NotFoundException("예약에 해당하는 결제 내역이 존재하지 않습니다."));
    }
}
