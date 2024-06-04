package roomescape.service.mapper;

import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT;
import static roomescape.fixture.ReservationFixture.DEFAULT_RESERVATION;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.dto.LoginMemberReservationResponse;

class LoginMemberReservationResponseMapperTest {

    @Test
    @DisplayName("도메인을 응답으로 잘 변환하는지 확인")
    void toResponse() {
        LoginMemberReservationResponse response = LoginMemberReservationResponseMapper
                .toResponse(DEFAULT_RESERVATION, DEFAULT_PAYMENT);

        Assertions.assertThat(response)
                .isEqualTo(new LoginMemberReservationResponse(
                        DEFAULT_RESERVATION.getId(),
                        DEFAULT_THEME.getName(),
                        DEFAULT_RESERVATION.getDate(),
                        DEFAULT_RESERVATION.getTime(),
                        "예약",
                        DEFAULT_PAYMENT.getPaymentKey(),
                        DEFAULT_PAYMENT.getAmount()
                ));
    }
}
