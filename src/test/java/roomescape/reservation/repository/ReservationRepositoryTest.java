package roomescape.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createMemberByName;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createReservation_1;
import static roomescape.TestFixture.createReservation_2;
import static roomescape.TestFixture.createTimeAt_10;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;

@DataJpaTest
@Import(DBHelper.class)
class ReservationRepositoryTest {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private ReservationRepository repository;

    @Test
    void 모든_예약_조회() {
        // given
        dbHelper.insertReservation(createReservation_1());
        dbHelper.insertReservation(createReservation_2());

        // when
        List<Reservation> reservations = repository.findByCriteria(null, null, null, null);

        // then
        assertThat(reservations).hasSize(2);
    }

    @Test
    void 특정_날짜로_예약_조회() {
        // given
        LocalDate date = DEFAULT_DATE;
        dbHelper.insertReservation(createReservationOf(createDefaultMember_1(), date, createTimeAt_10(), createDefaultTheme()));
        LocalDate otherDate = date.plusDays(1);
        dbHelper.insertReservation(createReservationOf(createDefaultMember_1(), otherDate, createTimeAt_10(), createDefaultTheme()));

        // when
        List<Reservation> reservations = repository.findByCriteria(null, null, date, date);

        // then
        SoftAssertions.assertSoftly(soft -> {
            assertThat(reservations).hasSize(1);
            assertThat(reservations.get(0).getDate()).isEqualTo(date);
        });
    }

    @Test
    void 회원_ID로_예약_조회() {
        // given
        Member member1 = createMemberByName("회원1");
        dbHelper.insertReservation(createReservationOf(member1, DEFAULT_DATE, createTimeAt_10(), createDefaultTheme()));
        dbHelper.insertReservation(createReservationOf(createMemberByName("회원2"), DEFAULT_DATE, createTimeAt_10(), createDefaultTheme()));


        // when
        List<Reservation> reservations = repository.findByCriteria(null, member1.getId(), null, null);

        // then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(reservations).hasSize(1);
            soft.assertThat(reservations.get(0).getMember().getId()).isEqualTo(member1.getId());
        });
    }
}
