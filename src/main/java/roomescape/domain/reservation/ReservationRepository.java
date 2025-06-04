package roomescape.domain.reservation;

import roomescape.domain.member.Member;
import roomescape.domain.reservationitem.ReservationItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findById(Long id);

    Reservation save(final Reservation reservation);

    void deleteById(final long id);

    List<Reservation> findByMemberIdAndThemeIdAndDateFromAndDateTo(final Long memberId,
                                                                   final Long themeId,
                                                                   final LocalDate dateFrom,
                                                                   final LocalDate dateTo);
    
    boolean existsByMemberAndReservationItem(Member member, ReservationItem reservationItem);

    List<Reservation> findByReservationStatusOrderByIdDesc(ReservationStatus reservationStatus);

    List<Reservation> findByMemberId(Long memberId);
}
