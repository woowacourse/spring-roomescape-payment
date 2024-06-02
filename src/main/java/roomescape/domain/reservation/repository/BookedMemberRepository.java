package roomescape.domain.reservation.repository;

import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.BookedMember;
import roomescape.domain.reservation.dto.BookedReservationReadOnly;

public interface BookedMemberRepository extends JpaRepository<BookedMember, Long> {

    @Query("""
            select new roomescape.domain.reservation.dto.BookedReservationReadOnly(
                bm.id,
                bm.member.name,
                r.date,
                r.time.startAt,
                r.theme.name
            )
            from BookedMember bm
            join bm.reservation r
            where (:memberId is null or bm.member.id = :memberId)
                and (:themeId is null or r.theme.id = :themeId)
                and (:startDate is null or r.date >= :startDate)
                and (:endDate is null or r.date <= :endDate)""")
    List<BookedReservationReadOnly> findByConditions(@Nullable LocalDate startDate, @Nullable LocalDate endDate, @Nullable Long themeId,
                                                     @Nullable Long memberId);

    @EntityGraph(attributePaths = {"reservation", "member"})
    List<BookedMember> findByMemberAndReservation_DateGreaterThanEqual(Member member, LocalDate date);
}
