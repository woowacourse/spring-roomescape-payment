package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ReservationStatusTest {

    @Test
    @DisplayName("요청 상태로 ReservationStatus를 생성한다.")
    void createReservationStatus_whenRequested_returnRequested() {
        // when
        ReservationStatus status = ReservationStatus.requested();

        // then
        assertThat(status.value()).isEqualTo(ReservationStatus.Status.REQUESTED);
    }

    @Test
    @DisplayName("확정 상태로 ReservationStatus를 생성한다.")
    void createReservationStatus_whenConfirmed_returnConfirmed() {
        // when
        ReservationStatus status = ReservationStatus.confirmed();

        // then
        assertThat(status.value()).isEqualTo(ReservationStatus.Status.CONFIRMED);
    }

    @Test
    @DisplayName("실패 상태로 ReservationStatus를 생성한다.")
    void createReservationStatus_whenFailed_returnFailed() {
        // when
        ReservationStatus status = ReservationStatus.failed();

        // then
        assertThat(status.value()).isEqualTo(ReservationStatus.Status.FAILED);
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.Status.class, names = {"CONFIRMED", "FAILED"})
    @DisplayName("완료된 상태인지 확인한다.")
    void isFinished_whenFinishedStatus_returnTrue(ReservationStatus.Status status) {
        // given
        ReservationStatus reservationStatus = new ReservationStatus(status);

        // when
        boolean isFinished = reservationStatus.isFinished();

        // then
        assertThat(isFinished).isTrue();
    }

    @Test
    @DisplayName("요청 상태는 완료되지 않은 상태이다.")
    void isFinished_whenRequested_returnFalse() {
        // given
        ReservationStatus status = ReservationStatus.requested();

        // when
        boolean isFinished = status.isFinished();

        // then
        assertThat(isFinished).isFalse();
    }

    @Test
    @DisplayName("확정 상태인지 확인한다.")
    void isConfirmed_whenConfirmed_returnTrue() {
        // given
        ReservationStatus status = ReservationStatus.confirmed();

        // when
        boolean isConfirmed = status.isConfirmed();

        // then
        assertThat(isConfirmed).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.Status.class, names = {"REQUESTED", "FAILED"})
    @DisplayName("확정되지 않은 상태인지 확인한다.")
    void isConfirmed_whenNotConfirmed_returnFalse(ReservationStatus.Status status) {
        // given
        ReservationStatus reservationStatus = new ReservationStatus(status);

        // when
        boolean isConfirmed = reservationStatus.isConfirmed();

        // then
        assertThat(isConfirmed).isFalse();
    }
}
