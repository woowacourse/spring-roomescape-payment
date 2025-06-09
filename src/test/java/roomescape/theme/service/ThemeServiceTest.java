package roomescape.theme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.TestFixture;
import roomescape.exception.ReservationException;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@Import({ThemeService.class, DBHelper.class})
class ThemeServiceTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ThemeService themeService;

    @Autowired
    DBHelper dbHelper;

    @Test
    void 테마가_저장된다() {
        // given
        ThemeRequest request = new ThemeRequest("이름3", "설명3", "썸네일3");

        // when
        ThemeResponse response = themeService.saveTheme(request);

        // then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(response.name()).isEqualTo(request.name());
            soft.assertThat(response.description()).isEqualTo(request.description());
            soft.assertThat(response.thumbnail()).isEqualTo(request.thumbnail());
        });
    }

    @Test
    void 모든_테마를_조회한다() {
        // given
        dbHelper.insertTheme(TestFixture.createThemeByName("테마1"));
        dbHelper.insertTheme(TestFixture.createThemeByName("테마2"));

        // when
        List<ThemeResponse> all = themeService.findAll();

        // then
        assertThat(all)
                .hasSize(2)
                .extracting(ThemeResponse::name)
                .containsExactly("테마1", "테마2");
    }

    @Test
    void 테마가_삭제된다() {
        // given
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        assertThat(themeRepository.findAll()).hasSize(1);

        // when
        themeService.delete(theme.getId());

        // then
        assertThat(themeRepository.findAll()).hasSize(0);
    }

    @Test
    void 예약이_존재하는_테마를_삭제하지_못_한다() {
        // given
        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        Reservation reservation = createReservationOf(
                createDefaultMember_1(),
                DEFAULT_DATE,
                createTimeAt_10(),
                theme
        );
        dbHelper.insertReservation(reservation);

        // when & then
        assertThatThrownBy(() -> themeService.delete(theme.getId()))
                .isInstanceOf(ReservationException.class)
                .hasMessage("해당 테마로 예약된 건이 존재합니다.");
    }
}
