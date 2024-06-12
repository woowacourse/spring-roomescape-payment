package roomescape.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDate;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    boolean existsByDateAndTimeIdAndThemeId(ReservationDate date, Long timeId, Long themeId);

    boolean existsByTimeId(Long id);

    List<Reservation> findAllByDateAndThemeId(ReservationDate date, Long themeId);

    boolean existsByThemeId(Long id);

    List<Reservation> findAllByMemberId(Long id);

    Optional<Reservation> findByDateAndTimeIdAndThemeId(ReservationDate date, Long timeId, Long themeId);

    @Query("""
            SELECT r FROM Reservation r
            JOIN FETCH r.payments p
            WHERE r.member.id = :id
            """)
    List<Reservation> findAllByMemberIdWithPayments(Long id);
}
