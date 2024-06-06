package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import roomescape.reservation.dto.SaveReservationTimeRequest;
import roomescape.reservation.model.ReservationTime;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;

    @DisplayName("전체 예약 시간 정보를 조회한다.")
    @Test
    void getReservationTimesTest() {
        // When
        final List<ReservationTime> reservationTimes = reservationTimeService.getReservationTimes();

        // Then
        assertThat(reservationTimes).hasSize(8);
    }

    @DisplayName("예약 시간 정보를 저장한다.")
    @Test
    void saveReservationTimeTest() {
        // Given
        final LocalTime startAt = LocalTime.parse("03:30");
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(startAt);

        // When
        final ReservationTime reservationTime = reservationTimeService.saveReservationTime(saveReservationTimeRequest);

        // Then
        final List<ReservationTime> reservationTimes = reservationTimeService.getReservationTimes();
        Assertions.assertAll(
                () -> assertThat(reservationTimes).hasSize(9),
                () -> assertThat(reservationTime.getId()).isEqualTo(9L),
                () -> assertThat(reservationTime.getStartAt()).isEqualTo(startAt)
        );
    }

    @DisplayName("예약 시간 정보를 삭제한다.")
    @Test
    void deleteReservationTimeTest() {
        // When
        reservationTimeService.deleteReservationTime(2L);

        // Then
        final List<ReservationTime> reservationTimes = reservationTimeService.getReservationTimes();
        assertThat(reservationTimes).hasSize(7);
    }

    @DisplayName("이미 존재하는 예약시간이 입력되면 예외를 발생한다.")
    @Test
    void throwExceptionWhenExistReservationTimeTest() {
        // Given
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(LocalTime.of(9, 30));
        // When & Then
        assertThatThrownBy(() -> reservationTimeService.saveReservationTime(saveReservationTimeRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 예약시간이 있습니다.");
    }

    @DisplayName("해당 시간을 참조하고 있는 예약이 하나라도 있으면 삭제시 예외가 발생한다.")
    @Test
    void throwExceptionWhenDeleteReservationTimeHasRelation() {
        // Given
        final long reservationTimeId = 1;
        // When & Then
        assertThatThrownBy(() -> reservationTimeService.deleteReservationTime(reservationTimeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약에 포함된 시간 정보는 삭제할 수 없습니다.");
    }
}