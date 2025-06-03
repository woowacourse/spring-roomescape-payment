package roomescape.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createMemberByName;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;
import static roomescape.TestFixture.createWaitingOf;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.member.domain.Member;
import roomescape.reservation.repository.dto.MemberRegistrationProjection;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import(DBHelper.class)
class RegistrationQueryRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    DBHelper dbHelper;

    @Autowired
    private RegistrationQueryRepository registrationQueryRepository;

    @DisplayName("예약 + 대기 통합 조회")
    @Test
    void query_allBookings() {
        // given
        Member member = createMemberByName("member1");
        Member member2 = createMemberByName("member2");
        Member member3 = createMemberByName("member3");
        ReservationTime time = createTimeAt_10();
        Theme theme = createDefaultTheme();

        dbHelper.insertReservation(createReservationOf(member2, DEFAULT_DATE, time, theme));
        dbHelper.insertWaiting(createWaitingOf(member, DEFAULT_DATE, time, theme));
        dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE.plusDays(1), time, theme));
        dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE.plusDays(2), time, theme));

        dbHelper.insertWaiting(createWaitingOf(member2, DEFAULT_DATE.plusDays(3), time, theme));
        dbHelper.insertWaiting(createWaitingOf(member3, DEFAULT_DATE.plusDays(3), time, theme));
        dbHelper.insertWaiting(createWaitingOf(member, DEFAULT_DATE.plusDays(3), time, theme));

        // when
        List<MemberRegistrationProjection> registrationsData =
                registrationQueryRepository.findAllRegistrationsByMemberId(member.getId());

        // then
        assertAll(
                () -> assertThat(registrationsData).hasSize(4),
                () -> assertThat(registrationsData).extracting(MemberRegistrationProjection::getType)
                        .containsExactly("WAITING", "BOOKED", "BOOKED", "WAITING"),
                () -> assertThat(registrationsData).extracting(MemberRegistrationProjection::getRank)
                        .containsExactly(1, 0, 0, 3)
        );
    }
}
