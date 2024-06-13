package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.system.exception.RoomEscapeException;
import roomescape.theme.domain.Theme;

class PaymentTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        LocalDate now = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(LocalTime.now());
        Theme theme = new Theme("name", "desc", "thumb");
        Member member = new Member("name", "email", "password", Role.MEMBER);

        reservation = new Reservation(now, reservationTime, theme, member, ReservationStatus.CONFIRMED);
    }

    @ParameterizedTest
    @DisplayName("paymentKey가 null 또는 빈값이면 예외가 발생한다.")
    @NullAndEmptySource
    void invalidPaymentKey(String paymentKey) {
        assertThatThrownBy(() -> new Payment("order-id", paymentKey, 10000L, reservation, OffsetDateTime.now()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @ParameterizedTest
    @DisplayName("orderId가 null 또는 빈값이면 예외가 발생한다.")
    @NullAndEmptySource
    void invalidOrderId(String orderId) {
        assertThatThrownBy(() -> new Payment(orderId, "payment-key", 10000L, reservation, OffsetDateTime.now()))
                .isInstanceOf(RoomEscapeException.class);
    }


    @ParameterizedTest
    @DisplayName("amount가 null 또는 0 이하면 예외가 발생한다.")
    @CsvSource(value = {"null", "-1"}, nullValues = {"null"})
    void invalidOrderId(Long totalAmount) {
        assertThatThrownBy(
                () -> new Payment("orderId", "payment-key", totalAmount, reservation, OffsetDateTime.now()))
                .isInstanceOf(RoomEscapeException.class);
    }


    @ParameterizedTest
    @DisplayName("Reservation이 null이면 예외가 발생한다.")
    @NullSource
    void invalidReservation(Reservation reservation) {
        assertThatThrownBy(() -> new Payment("orderId", "payment-key", 10000L, reservation, OffsetDateTime.now()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @ParameterizedTest
    @DisplayName("승인 날짜가 null이면 예외가 발생한다.")
    @NullSource
    void invalidApprovedAt(OffsetDateTime approvedAt) {
        assertThatThrownBy(() -> new Payment("orderId", "payment-key", 10000L, reservation, approvedAt))
                .isInstanceOf(RoomEscapeException.class);
    }
}
