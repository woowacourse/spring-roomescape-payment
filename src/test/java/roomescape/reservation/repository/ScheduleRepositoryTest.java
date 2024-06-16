package roomescape.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.reservation.domain.Schedule;
import roomescape.test.RepositoryTest;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.TimeRepository;

class ScheduleRepositoryTest extends RepositoryTest {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ThemeRepository themeRepository;

    @DisplayName("해당 날짜, 시간, 테마에 있는 스케줄을 찾을 수 있다.")
    @Test
    void findByDateAndTimeAndThemeTest() {
        LocalDate date = LocalDate.of(2022, 5, 5);
        ReservationTime time = timeRepository.findById(2L).get();
        Theme theme = themeRepository.findById(1L).get();

        Optional<Schedule> actual = scheduleRepository.findByDateAndTimeAndTheme(date, time, theme);

        assertThat(actual).isNotEmpty();
    }

    @DisplayName("해당 날짜, 시간, 테마에 있는 스케줄이 없을 경우, empty를 반환한다.")
    @Test
    void findByDateAndTimeAndThemeTest_whenScheduleNotExist() {
        LocalDate date = LocalDate.of(2022, 5, 5);
        ReservationTime time = timeRepository.findById(2L).get();
        Theme theme = themeRepository.findById(2L).get();

        Optional<Schedule> actual = scheduleRepository.findByDateAndTimeAndTheme(date, time, theme);

        assertThat(actual).isEmpty();
    }
}
