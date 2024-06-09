package roomescape.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.WaitingWithRank;

import java.time.LocalDate;
import java.util.List;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @Query("""
    select w
    from Waiting w
    join fetch w.member
    join fetch w.reservation
    """)
    List<Waiting> findAll();

    @Override
    void delete(Waiting entity);

    boolean existsByMemberAndReservation(Member member, Reservation reservation);

    @Query("select exists(select 1 from Waiting w where w=:waiting and w.member = :member)")
    boolean existsByWaitingAndMember(@Param("waiting") Waiting waiting, @Param("member") Member member);

    @Query("""
    select new roomescape.domain.reservation.WaitingWithRank(w,
      (select count(1)
       from Waiting w2
       where w2.reservation = w.reservation
         and w2.createAt < w.createAt))
    from Waiting w
    join fetch w.reservation r
    join fetch r.reservationSlot rs
    join fetch rs.time time
    join fetch rs.theme theme
    join fetch w.member member
    where member = :member
      and rs.date >= :date
    """)

    List<WaitingWithRank> findMemberWaitingWithRank(@Param("member") Member member, @Param("date") LocalDate date);

    List<Waiting> findByReservationOrderById(Reservation reservation);
}
