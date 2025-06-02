package roomescape.reservationslot.presentation.dto.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import roomescape.reservation.presentation.dto.request.ConfirmedReservationCreateWebRequest;

class ConfirmedReservationCreateWebRequestTest {

    @Test
    void create_shouldThrowException_whenDateNull() {
        assertThatThrownBy(
                () -> new ConfirmedReservationCreateWebRequest(
                        null,
                        1L,
                        1L,
                        "testtest",
                        "orderorder",
                        10000L
                )
        ).hasMessageContaining("날짜는 반드시 입력해야합니다.");
    }

    @Test
    void create_shouldThrowException_whenTimeIdNull() {
        assertThatThrownBy(
                () -> new ConfirmedReservationCreateWebRequest(
                        LocalDate.parse("2025-12-25"),
                        null,
                        1L,
                        "testtest",
                        "orderorder",
                        10000L
                )
        ).hasMessageContaining("timeId는 반드시 입력해야합니다.");
    }

    @Test
    void create_shouldThrowException_whenThemeIdNull() {
        assertThatThrownBy(
                () -> new ConfirmedReservationCreateWebRequest(
                        LocalDate.parse("2025-12-25"),
                        1L,
                        null,
                        "testtest",
                        "orderorder",
                        10000L
                )
        ).hasMessageContaining("themeId는 반드시 입력해야합니다.");
    }

    @Test
    void create_shouldThrowException_whenPaymentKeyNull() {
        assertThatThrownBy(
                () -> new ConfirmedReservationCreateWebRequest(
                        LocalDate.parse("2025-12-25"),
                        1L,
                        1L,
                        null,
                        "orderorder",
                        10000L
                )
        ).hasMessageContaining("paymentKey는 반드시 입력해야합니다.");
    }

    @Test
    void create_shouldThrowException_whenOrderIdNull() {
        assertThatThrownBy(
                () -> new ConfirmedReservationCreateWebRequest(
                        LocalDate.parse("2025-12-25"),
                        1L,
                        1L,
                        "testtest",
                        null,
                        10000L
                )
        ).hasMessageContaining("orderId는 반드시 입력해야합니다.");
    }

    @Test
    void create_shouldThrowException_whenAmountNull() {
        assertThatThrownBy(
                () -> new ConfirmedReservationCreateWebRequest(
                        LocalDate.parse("2025-12-25"),
                        1L,
                        1L,
                        "testtest",
                        "orderorder",
                        null
                )
        ).hasMessageContaining("amount는 반드시 입력해야합니다.");
    }

    @Test
    void create_shouldThrowException_whenDateformatIllegal() {
        assertThatThrownBy(
                () -> new ConfirmedReservationCreateWebRequest(
                        LocalDate.parse("2025-12"),
                        1L,
                        1L,
                        "testtest",
                        "orderorder",
                        10000L
                )
        );
    }
}
