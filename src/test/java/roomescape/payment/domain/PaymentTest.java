package roomescape.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.common.validate.InvalidInputException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.time.domain.ReservationTime;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Payment 도메인 테스트")
class PaymentTest {

    private static final String VALID_PAYMENT_KEY = "test_payment_key_123";
    private static final PaymentAmount VALID_AMOUNT = PaymentAmount.from(10000);
    private static final String VALID_ORDER_ID = "order_123456";

    @Test
    @DisplayName("Payment Key가 null이면 예외가 발생한다")
    void throwExceptionWhenPaymentKeyIsNull() {
        // given
        Reservation reservation = createReservation(1L);
        // when & then
        assertThatThrownBy(() -> Payment.of(
                null, VALID_AMOUNT, VALID_ORDER_ID, reservation))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Validation failed [while checking null]: Payment.paymentKey");
    }

    @Test
    @DisplayName("Order ID가 null이면 예외가 발생한다")
    void throwExceptionWhenOrderIdIsNull() {
        // given
        Reservation reservation = createReservation(1L);
        // when & then
        assertThatThrownBy(() -> Payment.of(
                VALID_PAYMENT_KEY, VALID_AMOUNT, null, reservation))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Validation failed [while checking null]: Payment.orderId");
        ;
    }

    @Test
    @DisplayName("amount가 null이면 예외가 발생한다")
    void throwExceptionWhenPaymentAmountIsNull() {
        // given
        Reservation reservation = createReservation(1L);
        // when & then
        assertThatThrownBy(() -> Payment.of(
                VALID_PAYMENT_KEY, null, VALID_ORDER_ID, reservation))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Validation failed [while checking null]: Payment.amount");
    }

    @Test
    @DisplayName("reservation가 null이면 예외가 발생한다")
    void throwExceptionWhenReservationIsNull() {
        // when & then
        assertThatThrownBy(() -> Payment.of(
                VALID_PAYMENT_KEY, VALID_AMOUNT, VALID_ORDER_ID, null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Validation failed [while checking null]: Payment.reservation");
    }

    private Reservation createReservation(Long id) {
        return new Reservation(
                id,
                1L,
                ReservationDate.from(LocalDate.now().plusDays(1)),
                new ReservationTime(
                        1L,
                        LocalTime.of(15, 0)
                ),
                new Theme(
                        1L,
                        ThemeName.from("테스트테마"),
                        ThemeDescription.from("설명"),
                        ThemeThumbnail.from("thumbnail.jpg")
                )
        );
    }
}
