package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.service.reservation.ReservationItemService;
import roomescape.test_util.ServiceTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationItemServiceTest extends ServiceTest {

    @Autowired
    private ReservationItemService reservationItemService;

    @Test
    @DisplayName("예약 아이템을 저장한다")
    void saveReservationItem() {
        // given
        ReservationTheme theme = insertReservationTheme("테마", "설명", "썸네일");
        ReservationTime time = insertReservationTime(LocalTime.of(12, 12));
        LocalDate date = LocalDate.now().plusDays(2L);

        // when
        final ReservationItem reservationItem = reservationItemService.createReservationItemIfNotExist(date, time, theme);

        // then
        assertAll(
                () -> assertThat(reservationItem.getDate()).isEqualTo(date),
                () -> assertThat(reservationItem.getTime().getId()).isEqualTo(time.getId()),
                () -> assertThat(reservationItem.getTheme().getId()).isEqualTo(theme.getId())
        );
    }

    @Test
    @DisplayName("이미 예약이 존재하는 경우 기존 예약을 가져온다.")
    void duplicateReservationItemTest() {
        // given
        ReservationTheme theme = insertReservationTheme("테마", "설명", "썸네일");
        ReservationTime time = insertReservationTime(LocalTime.of(12, 12));
        LocalDate date = LocalDate.now().plusDays(1L);
        ReservationItem item = insertReservationItem(date, time, theme);

        // when
        final ReservationItem findItem = reservationItemService.createReservationItemIfNotExist(date, time, theme);

        // then
        assertThat(findItem.getId()).isEqualTo(item.getId());
    }

    @Test
    @DisplayName("과거 날짜로 예약을 생성할 수 없다.")
    void notPastDateReservationItemTest() {
        // given
        ReservationTheme theme = insertReservationTheme("테마", "설명", "썸네일");
        ReservationTime time = insertReservationTime(LocalTime.of(12, 12));
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // when, then
        assertThatThrownBy(() -> reservationItemService.createReservationItemIfNotExist(yesterday, time, theme))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("과거 시간으로 예약을 생성할 수 없다.")
    void notPastTimeReservationItemTest() {
        // given
        ReservationTheme theme = insertReservationTheme("테마", "설명", "썸네일");
        ReservationTime oneHourBefore = insertReservationTime(LocalTime.now().minusHours(1));
        LocalDate today = LocalDate.now();

        // when, then
        assertThatThrownBy(() -> reservationItemService.createReservationItemIfNotExist(today, oneHourBefore, theme))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약의 아이템이 이미 존재하는지 확인한다")
    void existReservationItemTest() {
        // given
        ReservationTheme theme = insertReservationTheme("테마", "설명", "썸네일");
        ReservationTime time = insertReservationTime(LocalTime.of(12, 12));
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDate illegalDate = LocalDate.now().plusDays(5);
        insertReservationItem(date, time, theme);

        // when
        final boolean exist = reservationItemService.isExistReservationItem(date, time.getId(), theme.getId());
        final boolean nonExist = reservationItemService.isExistReservationItem(illegalDate, time.getId(), theme.getId());

        // then
        assertAll(
                () -> assertThat(exist).isTrue(),
                () -> assertThat(nonExist).isFalse()
        );
    }
}
