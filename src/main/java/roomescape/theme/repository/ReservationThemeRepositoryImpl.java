package roomescape.theme.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.theme.domain.ReservationTheme;

@Repository
public class ReservationThemeRepositoryImpl implements ReservationThemeRepository {

    private final ReservationThemeJpaRepository reservationThemeJpaRepository;

    public ReservationThemeRepositoryImpl(final ReservationThemeJpaRepository reservationThemeJpaRepository) {
        this.reservationThemeJpaRepository = reservationThemeJpaRepository;
    }

    @Override
    public Optional<ReservationTheme> findById(final Long id) {
        return reservationThemeJpaRepository.findById(id);
    }

    @Override
    public List<ReservationTheme> findAll() {
        return reservationThemeJpaRepository.findAll();
    }

    @Override
    public List<ReservationTheme> findWeeklyThemeOrderByCountDesc() {
        return reservationThemeJpaRepository.findWeeklyThemeOrderByCountDesc();
    }

    @Override
    public ReservationTheme save(final ReservationTheme reservationTheme) {
        return reservationThemeJpaRepository.save(reservationTheme);
    }

    @Override
    public void deleteById(final long id) {
        reservationThemeJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(final String name) {
        return reservationThemeJpaRepository.existsByName(name);
    }

    @Override
    public boolean existsById(final long id) {
        return reservationThemeJpaRepository.existsById(id);
    }
}
