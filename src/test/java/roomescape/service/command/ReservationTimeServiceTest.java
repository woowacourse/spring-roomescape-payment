package roomescape.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.test.fixture.DateFixture.TODAY;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import roomescape.exception.BadRequestException;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.reservation.domain.Reservation;
import roomescape.mvc.reservation.service.ReservationQueryService;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.mvc.time.dto.ReservationTimeCreationContent;
import roomescape.mvc.time.response.AddTimeResponse;
import roomescape.mvc.time.service.ReservationTimeQueryService;
import roomescape.mvc.time.service.ReservationTimeService;
import roomescape.mvc.waiting.domain.Waiting;
import roomescape.mvc.waiting.service.WaitingQueryService;

@DataJpaTest
@Import(value = {
        ReservationTimeQueryService.class, ReservationQueryService.class, WaitingQueryService.class,
        ReservationTimeService.class})
class ReservationTimeServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ReservationTimeService timeService;

    private Theme theme;
    private Member member;

    @BeforeEach
    void setup() {
        theme = entityManager.persist(
                Theme.createWithoutId("테마", "테마 설명", "thumbnail.jpg"));
        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("예약 시간을 추가할 수 있다.")
    public class addReservationTime {

        @DisplayName("예약 시간을 성공적으로 추가할 수 있다.")
        @Test
        void canAddReservationTime() {
            // given
            ReservationTimeCreationContent creationContent = new ReservationTimeCreationContent(LocalTime.of(10, 0));

            // when
            AddTimeResponse response = timeService.addReservationTime(creationContent);

            // then
            ReservationTime expectedTime = entityManager.find(ReservationTime.class, response.id());
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(expectedTime.getId()),
                    () -> assertThat(response.startAt()).isEqualTo(expectedTime.getStartAt())
            );
        }

        @DisplayName("중복된 에약 시간은 추가할 수 없다.")
        @Test
        void cannotAddDuplicatedReservationTime() {
            // given
            ReservationTime savedTime = entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(10, 0)));

            ReservationTimeCreationContent duplicationCreation =
                    new ReservationTimeCreationContent(savedTime.getStartAt());

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThatThrownBy(() -> timeService.addReservationTime(duplicationCreation))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("중복된 예약시간입니다.");
        }
    }

    @Nested
    @DisplayName("예약 시간을 삭제할 수 있다.")
    public class deleteReservationTimeById {

        @DisplayName("예약 시간을 성공적으로 추가할 수 있다.")
        @Test
        void canDeleteReservationTime() {
            // given
            ReservationTime time = entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(10, 0)));

            entityManager.flush();
            entityManager.clear();

            // when
            timeService.deleteReservationTimeById(time.getId());

            // then
            assertThat(entityManager.find(ReservationTime.class, time.getId())).isNull();
        }

        @DisplayName("이미 해당 시간에 예약이 존재할 경우 예약을 추가할 수 없다.")
        @Test
        void cannotDeleteReservedTimeByReservation() {
            // given
            ReservationTime time = entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(10, 0)));

            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(TODAY, time, theme, member));

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThatThrownBy(() -> timeService.deleteReservationTimeById(time.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 예약이 존재하는 예약 시간입니다.");
        }

        @DisplayName("이미 해당 시간에 예약 대기가 존재할 경우 예약을 추가할 수 없다.")
        @Test
        void cannotDeleteReservedTimeByWaiting() {
            // given
            ReservationTime time = entityManager.persist(ReservationTime.createWithoutId(LocalTime.of(10, 0)));

            entityManager.persist(Waiting.createWithoutIdWithoutPayment(TODAY, theme, time, member));

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThatThrownBy(() -> timeService.deleteReservationTimeById(time.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 예약 대기가 존재하는 예약 시간입니다.");
        }
    }
}
