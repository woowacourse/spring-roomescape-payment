package roomescape.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.test.fixture.DateFixture.TODAY;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.dto.business.ReservationTimeWithBookState;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.repository.ReservationTimeRepository;

@DataJpaTest
class ReservationTimeQueryServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ReservationTimeRepository timeRepository;

    private ReservationTimeQueryService timeQueryService;

    @BeforeEach
    void setup() {
        timeQueryService = new ReservationTimeQueryService(timeRepository);
    }

    @DisplayName("모든 예약을 조회할 수 있다.")
    @Test
    void canFindAllReservations() {
        // given
        entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(11, 0)));
        entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(12, 0)));

        entityManager.flush();

        // when
        List<ReservationTimeResponse> allReservationTimes = timeQueryService.findAllReservationTimes();

        // then
        assertThat(allReservationTimes).hasSize(3);
    }

    @DisplayName("특정 테마와 날짜에 대한 예약시간을 예약 가능 여부와 함께 조회할 수 있다.")
    @Test
    void canFindReservationsWithBookState() {
        // given
        Theme theme = entityManager.persist(
                Theme.createWithoutId("테마", "테마 설명", "thumbnail.jpg"));

        Member member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

        ReservationTime timeAt10 = entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        ReservationTime timeAt11 = entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(11, 0)));
        ReservationTime timeAt12 = entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(12, 0)));

        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, timeAt10, theme, member));
        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, timeAt11, theme, member));

        entityManager.flush();

        // when
        List<ReservationTimeWithBookState> timesWithBookState =
                timeQueryService.findReservationTimesWithBooking(theme.getId(), TODAY);

        // then
        assertAll(
                () -> assertThat(timesWithBookState)
                        .extracting(ReservationTimeWithBookState::id)
                        .containsExactly(timeAt10.getId(), timeAt11.getId(), timeAt12.getId()),
                () -> assertThat(timesWithBookState)
                        .extracting(ReservationTimeWithBookState::alreadyBooked)
                        .containsExactly(true, true, false)
        );
    }
}
