package roomescape.domain.reservation.reserved;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReservedRepository extends JpaRepository<Reserved, Long>, JpaSpecificationExecutor<Reserved> {

    @EntityGraph(attributePaths = {"theme", "user", "timeSlot", "payment"})
    List<Reserved> findByUserId(long id);

    @EntityGraph(attributePaths = {"timeSlot"})
    List<Reserved> findByDateAndThemeId(LocalDate date, long themeId);

    boolean existsByTimeSlotId(long timeSlotId);

    boolean existsByThemeId(long themeId);

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reserved r
            WHERE r.date = :date AND r.timeSlot.id = :timeSlotId AND r.theme.id = :themeId AND r.user.id = :userId
            OR EXISTS (
                SELECT w FROM Waiting w 
                WHERE w.date = :date AND w.timeSlot.id = :timeSlotId AND w.theme.id = :themeId AND w.user.id = :userId
            ) 
            OR EXISTS (
                SELECT p FROM PendingPayment p 
                WHERE p.date = :date AND p.timeSlot.id = :timeSlotId AND p.theme.id = :themeId AND p.user.id = :userId
            )
            """)
    boolean existsAnyReservationBy(
            LocalDate date,
            Long timeSlotId,
            Long themeId,
            Long userId
    );
}
