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
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.service.reservation.ReservationTimeService;
import roomescape.test_util.ServiceTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class ReservationTimeServiceTest extends ServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Test
    @DisplayName("예약 시간을 성공적으로 추가한다")
    void saveTest() {
        // given
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(12, 12));

        // when
        ReservationTimeResponse response = reservationTimeService.save(reservationTimeRequest);

        // then
        assertThat(response.startAt()).isEqualTo(LocalTime.of(12, 12));
    }

    @Test
    @DisplayName("예약 시간을 삭제한다")
    void removeTest() {
        // given
        ReservationTime savedTime = insertReservationTime(LocalTime.of(12, 12));

        // when, then
        assertThatCode(() -> reservationTimeService.remove(savedTime.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간을 삭제하면 예외가 발생한다.")
    void removeReferencedReservationTimeTest() {
        // given
        long id = 1L;

        // when, then
        assertThatThrownBy(() -> reservationTimeService.remove(id))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("모든 예약 시간을 검색한다")
    void findReservationTimesTest() {
        // given
        insertReservationTime(LocalTime.of(12, 12));

        // when
        List<ReservationTimeResponse> reservationTimes = reservationTimeService.getAll();

        // then
        assertThat(reservationTimes).hasSize(1);
    }

    @Test
    @DisplayName("이미 예약이 존재하는 상황에 시간을 삭제하려 하면 예외가 발생한다.")
    void deleteExistReservationTest() {
        // given
        ReservationTheme theme = insertReservationTheme("테마", "설명", "썸네일");
        ReservationTime time = insertReservationTime(LocalTime.of(12, 12));
        ReservationItem item = insertReservationItem(LocalDate.now().plusDays(1), time, theme);
        Member member = insertMember("이메일", "비밀번호", "이름", MemberRole.USER);
        insertReservation(member, item, ReservationStatus.PENDING);

        // when, then
        assertThatThrownBy(() -> reservationTimeService.remove(time.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
