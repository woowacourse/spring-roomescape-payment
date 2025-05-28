package roomescape.reservation.payment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;

class PaymentTest {

    @DisplayName("결제 정보가 존재하지 않으면 결제를 생성할 수 없다.")
    @MethodSource
    @ParameterizedTest
    void createWithNonExistsInfo(String paymentKey, String orderId, Long amount, Reservation reservation) {
        assertThatThrownBy(() -> new Payment(paymentKey, orderId, amount, reservation))
                .isInstanceOf(IllegalArgumentException.class);

    }

    private static Stream<Arguments> createWithNonExistsInfo() {
        Reservation reservation = new Reservation(
                new Member("하루", "haru@haru.com", "12341234", Role.ADMIN),
                LocalDate.now(),
                new ReservationTime(LocalTime.now()),
                new Theme("우테코", "우테코 테마", "www.woowacourse.com")
        );
        return Stream.of(
                Arguments.of(null, "orderId", 1_000L, reservation),
                Arguments.of("paymentKey", null, 1_000L, reservation),
                Arguments.of("paymentKey", "orderId", null, reservation),
                Arguments.of("paymentKey", "orderId", 1_000L, null)
        );
    }
}
