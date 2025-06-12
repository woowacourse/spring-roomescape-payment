package roomescape.reservation.repository;

import java.util.List;
import java.util.Optional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;

public interface ReservationRepository {

    boolean existsById(Long id);

    boolean existsByTimeId(Long timeId);

    boolean existsByParams(ReservationDate date, Long timeId, Long themeId);

    boolean existsByParams(ReservationDate date, Long timeId, Long themeId, Long memberId);

    Optional<Reservation> findById(Long id);

    List<Reservation> findByParams(ReservationDate date, Long themeId);

    List<Reservation> findByParams(Long memberId, Long themeId, ReservationDate from,
                                   ReservationDate to);

    List<Reservation> findAllByMemberId(Long memberId);

    List<Reservation> findAll();

    Reservation save(Reservation reservation);

    void deleteById(Long id);
}
