package roomescape.reservation.domain;

import roomescape.reservation.domain.vo.MyReservation;
import roomescape.reservation.domain.vo.ThemeBookingCount;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    boolean existsById(Long id);

    boolean existsByParams(Long timeId);

    boolean existsByParams(ReservationDate date, Long timeId, Long themeId);

    Optional<Reservation> findById(Long id);

    List<Long> findTimeIdByParams(ReservationDate date, Long themeId);

    List<Reservation> findAll();

    List<Reservation> findAllByUserId(Long userId);

    Reservation save(Reservation reservation);

    void deleteById(Long id);

    List<ThemeBookingCount> findThemesToBookedCount(ReservationDate startDate, ReservationDate endDate, int count);

    List<Reservation> findAllByParams(Long userId, Long themeId, ReservationDate reservationDate, ReservationDate reservationDate1);

    void updateUserId(Long id, Long userId);

    List<MyReservation> findMyReservationsByUserId(Long userId);

    Optional<Reservation> findBySlot(ReservationDate date, Long timeId, Long themeId);
}
