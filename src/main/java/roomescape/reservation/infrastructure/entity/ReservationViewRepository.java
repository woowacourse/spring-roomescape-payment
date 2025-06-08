package roomescape.reservation.infrastructure.entity;

import roomescape.reservation.domain.ReservationDate;

import java.util.Optional;

public interface ReservationViewRepository {

    boolean existsByParams(ReservationDate date, Long timeId, Long themeId, final Long userId);

    Optional<Long> findFirstWaitingByReservationId(Long id);
}
