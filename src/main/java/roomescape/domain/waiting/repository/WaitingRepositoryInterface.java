package roomescape.domain.waiting.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.member.entity.Member;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.time.entity.ReservationTime;
import roomescape.domain.waiting.entity.Waiting;

public interface WaitingRepositoryInterface {

    Waiting save(final Waiting waiting);

    boolean existsByDateAndTimeAndTheme(final LocalDate date, final ReservationTime reservationTime, final Theme theme);

    List<Waiting> findByMember(final Member member);

    Optional<Waiting> findById(final Long id);

    Optional<Waiting> findFirstByThemeAndDateAndTimeOrderByIdAsc(
            final Theme theme,
            final LocalDate date,
            final ReservationTime time
    );

    void deleteById(final Long id);

    long countBefore(final Theme theme, final LocalDate date, final ReservationTime time, final Long id);

    List<Waiting> findAll();
}
