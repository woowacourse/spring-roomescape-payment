package roomescape.reservation.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationStatusTest {

    @Test
    @DisplayName("예약대기 상태가 아닌지 판단한다.")
    void isNotWaiting() {
        // given
        List<ReservationStatus> statusesAllMatch = List.of(ReservationStatus.CONFIRMED, ReservationStatus.PAYMENT_REQUIRED);
        List<ReservationStatus> statusesNoneMatch = Stream.of(ReservationStatus.values())
                .filter(status -> !statusesAllMatch.contains(status))
                .toList();


        // when & then
        assertThat(statusesAllMatch).allMatch(ReservationStatus::isNotWaiting);
        assertThat(statusesNoneMatch).noneMatch(ReservationStatus::isNotWaiting);
    }

    @Test
    @DisplayName("예약된 상태인지 판단한다.")
    void isConfirmed() {
        // given
        List<ReservationStatus> statusesAllMatch = List.of(ReservationStatus.CONFIRMED);
        List<ReservationStatus> statusesNoneMatch = Stream.of(ReservationStatus.values())
                .filter(status -> !statusesAllMatch.contains(status))
                .toList();


        // when & then
        assertThat(statusesAllMatch).allMatch(ReservationStatus::isConfirmed);
        assertThat(statusesNoneMatch).noneMatch(ReservationStatus::isConfirmed);
    }

    @Test
    @DisplayName("결제대기 상태인지 판단한다.")
    void isPaymentRequired() {
        // given
        List<ReservationStatus> statusesAllMatch = List.of(ReservationStatus.PAYMENT_REQUIRED);
        List<ReservationStatus> statusesNoneMatch = Stream.of(ReservationStatus.values())
                .filter(status -> !statusesAllMatch.contains(status))
                .toList();


        // when & then
        assertThat(statusesAllMatch).allMatch(ReservationStatus::isPaymentRequired);
        assertThat(statusesNoneMatch).noneMatch(ReservationStatus::isPaymentRequired);
    }
}