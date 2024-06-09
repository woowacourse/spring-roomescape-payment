package roomescape.service.mapper;

import static roomescape.fixture.ReservationFixture.DEFAULT_RESERVATION;
import static roomescape.fixture.ReservationWaitingFixture.DEFAULT_WAITING;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.dto.LoginMemberWaitingResponse;

class LoginMemberWaitingResponseMapperTest {

    @Test
    @DisplayName("예약 대기 응답을 내 예약 조회 응답으로 잘 변환하는지 확인")
    void from() {
        LoginMemberWaitingResponse response = LoginMemberWaitingResponseMapper
                .from(ReservationWaitingResponseMapper.toResponse(DEFAULT_WAITING, 1));

        Assertions.assertThat(response)
                .isEqualTo(new LoginMemberWaitingResponse(
                        DEFAULT_RESERVATION.getId(),
                        DEFAULT_THEME.getName(),
                        DEFAULT_RESERVATION.getDate(),
                        DEFAULT_RESERVATION.getTime(),
                        1
                ));
    }
}
