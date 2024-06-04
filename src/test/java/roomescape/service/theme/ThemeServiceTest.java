package roomescape.service.theme;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.exception.InvalidReservationException;
import roomescape.fixture.*;
import roomescape.service.ServiceTest;
import roomescape.service.theme.dto.ThemeRequest;
import roomescape.service.theme.dto.ThemeResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThemeServiceTest extends ServiceTest {
    @Autowired
    private ThemeService themeService;

    @DisplayName("테마를 생성한다.")
    @Test
    void create() {
        //given
        ThemeRequest themeRequest = ThemeFixture.createThemeRequest();

        //when
        ThemeResponse themeResponse = themeService.create(themeRequest);

        //then
        assertThat(themeResponse.id()).isNotZero();
    }

    @DisplayName("중복된 테마를 생성할 수 없다.")
    @Test
    void cannotCreateByDuplicatedName() {
        //given
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ThemeRequest themeRequest = ThemeFixture.createThemeRequest(theme.getName().getValue());

        //when&then
        assertThatThrownBy(() -> themeService.create(themeRequest))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이미 존재하는 테마 이름입니다.");
    }

    @DisplayName("모든 테마를 조회한다.")
    @Test
    void findAll() {
        //given
        themeRepository.save(ThemeFixture.createTheme());

        //when
        List<ThemeResponse> responses = themeService.findAll();

        //then
        assertThat(responses).hasSize(1);
    }

    @DisplayName("테마를 삭제한다.")
    @Test
    void deleteById() {
        //given
        Theme theme = themeRepository.save(ThemeFixture.createTheme());

        //when
        themeService.deleteById(theme.getId());

        //then
        assertThat(themeService.findAll()).isEmpty();
    }

    @DisplayName("예약이 존재하는 테마를 삭제하면 예외가 발생한다.")
    @Test
    void cannotDeleteByReservation() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));

        //when&then
        assertThatThrownBy(() -> themeService.deleteById(theme.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("해당 테마로 예약(대기)이 존재해서 삭제할 수 없습니다.");
    }
}
