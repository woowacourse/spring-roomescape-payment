package roomescape.domain.reservation;

import java.util.List;
import java.util.Optional;
import roomescape.domain.member.Member;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);

    List<Reservation> findAll();

    List<Reservation> findAllByThemeAndDate(Theme theme, ReservationDate date);

    List<Reservation> findAllByMemberAndThemeAndDateBetween(Member member, Theme theme, ReservationDate from,
                                                            ReservationDate to);

    List<Reservation> findAllByReservationStatus(ReservationStatus reservationStatus);

    List<Reservation> findAllByDateAndReservationTimeAndThemeAndReservationStatusOrderByAsc(
            ReservationDate date,
            ReservationTime reservationTime,
            Theme theme,
            ReservationStatus reservationStatus
    );

    List<WaitingRankReservation> findAllWaitingRankByMember(Member member);

    void deleteById(Long id);

    boolean existsByReservationTime(ReservationTime reservationTime);

    boolean existsByDuplicateMember(
            ReservationDate date,
            ReservationTime reservationTime,
            Theme theme,
            Member member
    );

    boolean existsDuplicateStatus(
            ReservationTime reservationTime,
            ReservationDate date,
            Theme theme,
            ReservationStatus reservationStatus
    );

    boolean existsByTheme(Theme theme);
}
