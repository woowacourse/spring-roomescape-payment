package roomescape.domain.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.WaitingMember;
import roomescape.domain.reservation.dto.WaitingRank;
import roomescape.domain.reservation.dto.WaitingReadOnly;

public interface WaitingMemberRepository extends JpaRepository<WaitingMember, Long> {

    @Query("""
            select new roomescape.domain.reservation.dto.WaitingReadOnly(
            w.id,
            w.member.name,
            w.reservation.date,
            w.reservation.time.startAt,
            w.reservation.theme.name
            )
            from WaitingMember w""")
    List<WaitingReadOnly> findAllReadOnly();

    @Query("""
        select new roomescape.domain.reservation.dto.WaitingRank(w, count(1))
        from WaitingMember w
        inner join WaitingMember w2
        on w.member = :member
        and w.reservation.date >= :date
        and w.reservation = w2.reservation
        join fetch w.reservation
        join fetch w.reservation.time
        join fetch w.reservation.theme
        where w2.id <= w.id
        group by w.reservation
    """)
    List<WaitingRank> findRankByMemberAndDateGreaterThanEqual(Member member, LocalDate date);


}
