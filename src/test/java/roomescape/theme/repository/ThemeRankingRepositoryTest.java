package roomescape.theme.repository;


import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.TestFixture;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import(DBHelper.class)
class ThemeRankingRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    DBHelper dbHelper;

    @Test
    void 인기있는_테마들을_조회한다() {
        // given
        Theme theme1 = dbHelper.insertTheme(TestFixture.createThemeByName("테마1"));
        Theme theme2 = dbHelper.insertTheme(TestFixture.createThemeByName("테마2"));
        Theme theme3 = dbHelper.insertTheme(TestFixture.createThemeByName("테마3"));

        // theme1 예약 1개, theme3 예약 0개, theme2 예약 2개
        dbHelper.insertReservation(
                createReservationOf(createDefaultMember_1(), DEFAULT_DATE, createTimeAt_10(), theme2));
        dbHelper.insertReservation(
                createReservationOf(createDefaultMember_1(), DEFAULT_DATE, createTimeAt_10(), theme2));
        dbHelper.insertReservation(
                createReservationOf(createDefaultMember_1(), DEFAULT_DATE, createTimeAt_10(), theme1));

        // when
        LocalDate startDate = DEFAULT_DATE;
        LocalDate endDate = DEFAULT_DATE;
        final List<Theme> themes = themeRepository.findAllPopular(startDate, endDate);

        // then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(themes).hasSize(3);
            assertThat(themes)
                    .extracting(Theme::getName)
                    .containsExactly("테마2", "테마1", "테마3");
        });
    }
}
