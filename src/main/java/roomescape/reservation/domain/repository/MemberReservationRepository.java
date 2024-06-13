package roomescape.reservation.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.MemberReservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.domain.ReservationStatus;

public interface MemberReservationRepository extends JpaRepository<MemberReservation, Long> {

    @Query("""
                SELECT mr
                FROM MemberReservation mr
                WHERE (:memberId IS NULL OR mr.member.id = :memberId) 
                     AND (:themeId IS NULL OR mr.reservation.theme.id = :themeId)
                    AND :startDate <= mr.reservation.date 
                    AND mr.reservation.date <= :endDate
                    AND mr.reservationStatus = :status
            """)
    List<MemberReservation> findBy(Long memberId, Long themeId, ReservationStatus status, LocalDate startDate,
                                   LocalDate endDate);

    @Query("""
            SELECT mr
            FROM MemberReservation mr
            JOIN FETCH mr.reservation r
            JOIN FETCH r.time
            JOIN FETCH r.theme
            WHERE mr.member.id = :memberId
            """)
    List<MemberReservation> findByMemberId(long memberId);

    List<MemberReservation> findAllByReservation(ReservationInfo reservation);

    List<MemberReservation> findAllByReservationStatus(ReservationStatus reservationStatus);

    boolean existsByReservationAndMember(ReservationInfo reservation, Member member);

    boolean existsByReservationAndReservationStatus(ReservationInfo reservation, ReservationStatus reservationStatus);

    Optional<MemberReservation> findFirstByReservationOrderByCreatedAt(ReservationInfo reservation);

    boolean existsByReservationThemeId(long themeId);

    boolean existsByReservationTimeId(long timeId);
}
