package roomescape.service.reservation.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.USER_ID;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.service.ServiceBaseTest;

@Sql("/test-data.sql")
class ReservationMapperTest extends ServiceBaseTest {

    @Autowired
    ReservationMapper reservationMapper;

    @Test
    void 주어진_요청으로_예약을_매팽() {
        //given
        ReservationRequest request = new ReservationRequest(LocalDate.now(), 1L, 1L, USER_ID);

        //when
        Reservation reservation = reservationMapper.mapperOf(request, Status.RESERVED);

        //then
        assertAll(
                () -> assertThat(reservation.getDate()).isEqualTo(request.date()),
                () -> assertThat(reservation.getTime().getId()).isEqualTo(request.timeId()),
                () -> assertThat(reservation.getTheme().getId()).isEqualTo(request.themeId()),
                () -> assertThat(reservation.getMember().getId()).isEqualTo(request.memberId())
        );
    }
}
