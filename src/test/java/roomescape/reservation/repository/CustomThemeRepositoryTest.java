package roomescape.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.reservation.model.ReservationDate;
import roomescape.reservation.model.Theme;

@SpringBootTest
@Sql(value = "classpath:test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class CustomThemeRepositoryTest {

    @Autowired
    private CustomThemeRepository customThemeRepository;

    @DisplayName("특정 기간 중 가장 예약 개수가 많은 상위 10개의 테마 정보를 인기순으로 조회한다.")
    @Test
    void findPopularThemes() {
        // When
        final ReservationDate startAt = new ReservationDate(LocalDate.now().minusDays(7));
        final ReservationDate endAt = new ReservationDate(LocalDate.now().minusDays(1));
        final int maximumThemeCount = 10;

        final List<Theme> popularThemes = customThemeRepository.findPopularThemes(startAt, endAt, maximumThemeCount);

        // Then
        assertAll(
                () -> assertThat(popularThemes).hasSizeLessThanOrEqualTo(10),
                () -> assertThat(popularThemes.get(0).getId()).isEqualTo(1),
                () -> assertThat(popularThemes.get(1).getId()).isEqualTo(2),
                () -> assertThat(popularThemes.get(2).getId()).isEqualTo(10)
        );
    }
}
