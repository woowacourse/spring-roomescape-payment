package roomescape.domain.reservation.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.domain.reservation.dto.SaveReservationTimeRequest;
import roomescape.domain.reservation.dto.ReservationTimeDto;
import roomescape.domain.reservation.model.ReservationTime;
import roomescape.domain.reservation.model.ReservationTimeAvailabilities;
import roomescape.domain.reservation.service.ReservationTimeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;

    @DisplayName("전체 예약 시간 정보를 조회한다.")
    @Test
    void getReservationTimesTest() {
        // When
        final List<ReservationTimeDto> reservationTimes = reservationTimeService.getReservationTimes();

        // Then
        assertThat(reservationTimes).hasSize(8);
    }

    @DisplayName("예약 시간 정보를 저장한다.")
    @Test
    void saveReservationTimeTest() {
        // Given
        final LocalTime startAt = LocalTime.now().plusHours(0);
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(startAt);

        // When
        final ReservationTimeDto reservationTime = reservationTimeService.saveReservationTime(saveReservationTimeRequest);

        // Then
        final List<ReservationTimeDto> reservationTimes = reservationTimeService.getReservationTimes();
        Assertions.assertAll(
                () -> assertThat(reservationTimes).hasSize(9),
                () -> assertThat(reservationTime.id()).isEqualTo(9L),
                () -> assertThat(reservationTime.startAt()).isEqualTo(startAt)
        );
    }

    @DisplayName("예약 시간 정보를 삭제한다.")
    @Test
    void deleteReservationTimeTest() {
        // When
        reservationTimeService.deleteReservationTime(2L);

        // Then
        final List<ReservationTimeDto> reservationTimes = reservationTimeService.getReservationTimes();
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

    @DisplayName("예약 가능한 시간들을 조회한다.")
    @Test
    void getAvailableReservationTimesTest() {
        // Given
        final LocalDate date = LocalDate.now().plusDays(3);
        final Long themeId = 10L;

        // When
        final ReservationTimeAvailabilities availableReservationTimes = reservationTimeService.getAvailableReservationTimes(date, themeId);

        // Then
        final Map<ReservationTime, Boolean> values = availableReservationTimes.values();
        Assertions.assertAll(
                () -> assertThat(values).hasSize(8),
                () -> assertThat(values.values().contains(false)).isTrue()
        );
    }
}
