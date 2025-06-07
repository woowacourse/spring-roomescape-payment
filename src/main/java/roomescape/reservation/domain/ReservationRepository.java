package roomescape.reservation.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.reservation.dto.ReservationWithPayment;

public interface ReservationRepository {

    boolean existsByTimeId(Long timeId);

    boolean existByThemeId(Long themeId);

    Reservation save(Reservation reservation);

    void deleteById(Long id);

    List<Reservation> findAll();

    List<Reservation> findAllByDateAndThemeId(LocalDate date, Long themeId);

    List<Reservation> findAllByMemberIdAndThemeIdAndDateBetween(Long memberId, Long themeId, LocalDate from,
                                                                LocalDate to);

    List<ReservationWithPayment> findAllWithPaymentByMemberId(Long memberId);

    List<Reservation> findAllWithoutPaymentByMemberId(Long memberId);

    Optional<Reservation> findBy(LocalDate date, Long timeId, Long themeId);

    Optional<Reservation> findById(Long id);
}
