package roomescape.reservation.domain;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import roomescape.reservation.exception.InvalidReservationException;

class CreatedAtTest {

    @Test
    @DisplayName("유효한 생성 시간으로 CreatedAt을 생성한다.")
    void createCreatedAt_whenValidRequest_returnCreatedAt() {
        // given
        LocalDateTime validDateTime = LocalDateTime.now();

        // when
        CreatedAt createdAt = CreatedAt.from(validDateTime);

        // then
        assertThat(createdAt.value()).isEqualTo(validDateTime);
    }

    @Test
    @DisplayName("현재 시간으로 CreatedAt을 생성한다.")
    void createCreatedAt_whenNow_returnCurrentTime() {
        // when
        CreatedAt createdAt = CreatedAt.now();

        // then
        assertThat(createdAt.value()).isNotNull();
    }

    @Test
    @DisplayName("생성 시간이 null이면 예외가 발생한다.")
    void createCreatedAt_whenNull_throwsException() {
        // when & then
        assertThatThrownBy(() -> CreatedAt.from(null))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("생성 시간은 null일 수 없습니다.");
    }
}