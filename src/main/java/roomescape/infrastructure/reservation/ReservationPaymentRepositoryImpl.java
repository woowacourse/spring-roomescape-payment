package roomescape.infrastructure.reservation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.ReservationPaymentRepository;

@Repository
@RequiredArgsConstructor
public class ReservationPaymentRepositoryImpl implements ReservationPaymentRepository {

    private final ReservationPaymentJpaRepository reservationPaymentJpaRepository;

    @Override
    public void save(final ReservationPayment reservationPayment) {
        reservationPaymentJpaRepository.save(reservationPayment);
    }

    @Override
    public List<ReservationPayment> findAllByMember(final Member member) {
        return reservationPaymentJpaRepository.findAllByMember(member);
    }
}
