package roomescape.reservation.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.MemberReservation;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

public interface MemberReservationRepository extends JpaRepository<MemberReservation, Long> {

    @Query("""
                SELECT mr
                FROM MemberReservation mr
                JOIN FETCH mr.reservation r
                JOIN FETCH mr.member m
                JOIN FETCH r.time t
                JOIN FETCH r.theme th
                WHERE (:memberId IS NULL OR m.id = :memberId) 
                    AND (:themeId IS NULL OR th.id = :themeId) 
                    AND :startDate <= r.date 
                    AND r.date <= :endDate
            """)
    List<MemberReservation> findBy(Long memberId, Long themeId, LocalDate startDate,
                                   LocalDate endDate);

    List<MemberReservation> findByMemberId(long memberId);

    List<MemberReservation> findAllByReservationId(long reservationId);

    List<MemberReservation> findAllByReservationStatus(ReservationStatus reservationStatus);

    void deleteByReservationId(long reservationId);

    boolean existsByReservationAndMember(Reservation reservation, Member member);

    boolean existsByReservationAndReservationStatus(Reservation reservation, ReservationStatus reservationStatus);

    Optional<MemberReservation> findFirstByReservationOrderByCreatedAt(Reservation reservation);
}
