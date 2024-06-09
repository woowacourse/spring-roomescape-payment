package roomescape.service.mapper;

import static roomescape.dto.ReservationStatus.SUCCESS;
import static roomescape.dto.ReservationStatus.WAITING_PAYMENT;
import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT;
import static roomescape.fixture.ReservationFixture.DEFAULT_RESERVATION;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.dto.LoginMemberReservationResponse;

class LoginMemberReservationResponseMapperTest {

    @Test
    @DisplayName("예약과 결제 도메인을 응답으로 잘 변환하는지 확인")
    void toResponse() {
        LoginMemberReservationResponse response = LoginMemberReservationResponseMapper
                .toResponse(DEFAULT_RESERVATION, DEFAULT_PAYMENT);

        Assertions.assertThat(response)
                .isEqualTo(new LoginMemberReservationResponse(
                        DEFAULT_RESERVATION.getId(),
                        DEFAULT_THEME.getName(),
                        DEFAULT_RESERVATION.getDate(),
                        DEFAULT_RESERVATION.getTime(),
                        SUCCESS,
                        DEFAULT_PAYMENT.getPaymentKey(),
                        DEFAULT_PAYMENT.getAmount()
                ));
    }

    @Test
    @DisplayName("결제 없이 예약 도메인을 응답으로 잘 변환하는지 확인")
    void toNonPaidResponse() {
        LoginMemberReservationResponse response = LoginMemberReservationResponseMapper
                .toResponse(DEFAULT_RESERVATION, null);

        Assertions.assertThat(response)
                .isEqualTo(new LoginMemberReservationResponse(
                        DEFAULT_RESERVATION.getId(),
                        DEFAULT_THEME.getName(),
                        DEFAULT_RESERVATION.getDate(),
                        DEFAULT_RESERVATION.getTime(),
                        WAITING_PAYMENT,
                        null,
                        null
                ));
    }
}
