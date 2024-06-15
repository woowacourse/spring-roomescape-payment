package roomescape.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReservationStatusTest {

    @DisplayName("Rank 값을 받아 현재 예약 대기 상태를 반환한다.")
    @Test
    void createStatusMessage() {
        //given
        Long rank = 2L;

        //when
        String createdMessage = ReservationStatus.WAITING.createStatusMessage(rank);

        //then
        assertThat(createdMessage).isEqualTo("2번째 예약 대기");
    }

    @Test
    @DisplayName("상태가 예약 대기인지 판단한다.")
    void checkWaiting() {
        assertAll(
                () -> assertThat(ReservationStatus.WAITING.isWaiting()).isTrue(),
                () -> assertThat(ReservationStatus.BOOKING.isWaiting()).isFalse(),
                () -> assertThat(ReservationStatus.PENDING.isWaiting()).isFalse()
        );
    }
}
