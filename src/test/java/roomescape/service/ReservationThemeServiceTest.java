package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.service.reservation.ReservationThemeService;
import roomescape.test_util.ServiceTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationThemeServiceTest extends ServiceTest {

    @Autowired
    private ReservationThemeService reservationThemeService;

    @Test
    @DisplayName("모든 테마를 다 가져온다.")
    void getAllTest() {
        // given
        insertReservationTheme("테마", "설명", "썸네일");

        // when
        final List<ReservationThemeResponse> result = reservationThemeService.getAll();

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).name()).isEqualTo("테마"),
                () -> assertThat(result.get(0).description()).isEqualTo("설명"),
                () -> assertThat(result.get(0).thumbnail()).isEqualTo("썸네일")
        );

    }

    @Test
    void getPopulars() {
    }

    @Test
    @DisplayName("예약 테마를 저장한다.")
    void saveTest() {
        // given
        final ReservationThemeRequest request = new ReservationThemeRequest("테마", "설명", "썸네일");

        // when
        final ReservationThemeResponse result = reservationThemeService.save(request);

        // then
        assertAll(
                () -> assertThat(result.name()).isEqualTo("테마"),
                () -> assertThat(result.description()).isEqualTo("설명"),
                () -> assertThat(result.thumbnail()).isEqualTo("썸네일")
        );

    }

    @Test
    @DisplayName("존재하지 않는 예약 테마를 삭제하여 예외가 발생한다.")
    void deleteTest1() {
        // given
        final long id = 1L;

        // when, then
        assertThatThrownBy(() -> reservationThemeService.remove(id))
                .isInstanceOf(NoSuchElementException.class);

    }

    @Test
    @DisplayName("존재하는 예약 테마를 삭제하여 예외가 발생 하지 않는다.")
    void deleteTest2() {
        // given
        ReservationTheme savedTheme = insertReservationTheme("테마", "설명", "썸네일");

        // when, then
        assertThatCode(() -> reservationThemeService.remove(savedTheme.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("참조가 있는 테마를 삭제하려고 할 시 예외가 발생한다.")
    void deleteTest3() {
        // given
        ReservationTheme theme = insertReservationTheme("테마", "설명", "썸네일");
        ReservationTime time = insertReservationTime(LocalTime.of(12, 12));
        ReservationItem item = insertReservationItem(LocalDate.now().plusDays(1), time, theme);
        Member member = insertMember("이메일", "비밀번호", "이름", MemberRole.USER);
        insertReservation(member, item, ReservationStatus.PENDING);

        // when, then
        assertThatThrownBy(() -> reservationThemeService.remove(theme.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
