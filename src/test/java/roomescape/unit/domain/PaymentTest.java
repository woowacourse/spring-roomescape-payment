package roomescape.unit.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import roomescape.auth.Role;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.PaymentRequest;
import roomescape.exception.ArgumentNullException;

import java.time.LocalDate;
import java.time.LocalTime;

public class PaymentTest {
    @ParameterizedTest
    @CsvSource(value = {"null", "''"}, nullValues = "null")
    void 결제_키가_빈_값인_경우_예외가_발생한다(String paymentKey) {
        PaymentRequest request = new PaymentRequest(paymentKey, 1000, "orderId");
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().plusHours(1));

        Reservation reservation = Reservation.of(1L, new Member(1L, "member", "member@test.com", "password", Role.MEMBER), LocalDate.now(), reservationTime, new Theme(1L, "theme name", "theme description", "thumbnail"));

        Assertions.assertThatThrownBy(
                () -> new Payment(request, reservation)
        ).isInstanceOf(ArgumentNullException.class);
    }

    @Test
    void 예약_정보가_빈_값인_경우_예외가_발생한다() {
        PaymentRequest request = new PaymentRequest("paymentKey", 1000, "orderId");

        Assertions.assertThatThrownBy(
                () -> new Payment(request, null)
        ).isInstanceOf(ArgumentNullException.class);
    }
}
