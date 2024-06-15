package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class WaitingTest {

    @Test
    @DisplayName("예약 대기 상태이면 결제 대기 상태로 변경할 수 있다.")
    void changeStatusToPending() {
        Waiting waiting = new Waiting(
                1L,
                new Member(1L, "name", "email", "password", Role.ADMIN),
                LocalDate.of(2025, 12, 20),
                new TimeSlot(1L, LocalTime.of(12, 20)),
                new Theme(1L, "name", "description", "thumbnail", BigDecimal.valueOf(10000)),
                ReservationStatus.WAITING);

        waiting.toPending();

        assertThat(waiting.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.class, names = {"PENDING", "BOOKING"})
    @DisplayName("예약 대기 상태가 아닌데 결제 대기 상태로 변경하려 하면 예외가 발생한다.")
    void throwException_whenChangeStatusToPending(ReservationStatus status) {
        Waiting waiting = new Waiting(
                1L,
                new Member(1L, "name", "email", "password", Role.ADMIN),
                LocalDate.of(2025, 12, 20),
                new TimeSlot(1L, LocalTime.of(12, 20)),
                new Theme(1L, "name", "description", "thumbnail", BigDecimal.valueOf(10000)),
                status);

        assertThatThrownBy(waiting::toPending).isInstanceOf(IllegalStateException.class)
                .hasMessage("[ERROR] 결제 대기로 변경될 수 없습니다.");
    }
}
