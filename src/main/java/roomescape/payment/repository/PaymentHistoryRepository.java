package roomescape.payment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.model.Member;
import roomescape.payment.model.PaymentHistory;
import roomescape.reservation.model.Reservation;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query("SELECT ph FROM PaymentHistory ph JOIN FETCH ph.reservation r JOIN FETCH r.member WHERE r.member=:member")
    List<PaymentHistory> findAllByReservation_Member(Member member);

    Optional<PaymentHistory> findByReservation(Reservation reservation);
}
