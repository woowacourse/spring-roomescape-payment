package roomescape.infrastructure.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.ReservationPayment;

public interface ReservationPaymentJpaRepository extends JpaRepository<ReservationPayment, Long>{
    @Query(value = """
    SELECT p
    FROM ReservationPayment p
        JOIN FETCH p.reservation r
        JOIN FETCH p.reservation.theme
        JOIN FETCH p.reservation.reservationTime
    WHERE r.member = :member
    """)
    List<ReservationPayment> findAllByMember(final Member member);
}
