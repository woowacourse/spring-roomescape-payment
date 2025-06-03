package roomescape.reservationtime.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import(DBHelper.class)
class ReservationTimeRepositoryTest {

    @Autowired
    private ReservationTimeRepository repository;

    @Autowired
    DBHelper dbHelper;

    @Test
    void 예약가능한_모든_시간을_조회한다() {
        // given
        ReservationTime time1 = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        ReservationTime time2 = dbHelper.insertTime(createTimeAt(LocalTime.of(11, 0)));
        ReservationTime time3 = dbHelper.insertTime(createTimeAt(LocalTime.of(12, 0)));

        LocalDate date = DEFAULT_DATE;
        Theme theme = createDefaultTheme();
        dbHelper.insertReservation(createReservationOf(createDefaultMember_1(), date, time1, theme));

        // when
        List<AvailableReservationTimeResponse> allAvailable = repository.findAllAvailable(
                date, theme.getId());

        // then
        assertThat(allAvailable).hasSize(3)
                .extracting(AvailableReservationTimeResponse::alreadyBooked)
                .containsExactly(true, false, false);
    }
}
