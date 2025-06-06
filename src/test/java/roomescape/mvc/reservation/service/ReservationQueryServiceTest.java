package roomescape.mvc.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.test.fixture.DateFixture.NEXT_DAY;
import static roomescape.test.fixture.DateFixture.TODAY;
import static roomescape.test.fixture.DateFixture.YESTERDAY;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.reservation.domain.Reservation;
import roomescape.mvc.reservation.repository.ReservationRepository;
import roomescape.mvc.reservation.response.FindAllReservationResponse;
import roomescape.mvc.reservation.response.FindReservationsByFilter;
import roomescape.mvc.reservation.response.MineReservationResponse;
import roomescape.mvc.reservation.response.ReservationStatusResponse;
import roomescape.mvc.reservation.response.WaitingWithRankResponse;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.mvc.waiting.domain.Waiting;

@DataJpaTest
@Import(value = {ReservationQueryService.class})
class ReservationQueryServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationQueryService reservationQueryService;

    private ReservationTime reservationTime;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setup() {
        reservationTime = entityManager.persist(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        theme = entityManager.persist(
                Theme.createWithoutId("테마", "테마 설명", "thumbnail.jpg"));
        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("모든 예약을 조회할 수 있다.")
    @Test
    void canFindAll() {
        // given
        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, reservationTime, theme, member));
        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, reservationTime, theme, member));
        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, reservationTime, theme, member));

        entityManager.flush();
        entityManager.clear();

        // when
        List<FindAllReservationResponse> allReservations = reservationQueryService.findAllReservations();

        // then
        assertThat(allReservations).hasSize(3);
    }

    @DisplayName("회원의 모든 예약 상태를 조회할 수 있다.")
    @Test
    void canFindAllReservationStatusByMember() {
        // given
        List<Reservation> reservations = List.of(
                entityManager.persist(
                        Reservation.createWithoutIdAndPaymentHistory(TODAY, reservationTime, theme, member)),
                entityManager.persist(
                        Reservation.createWithoutIdAndPaymentHistory(TODAY, reservationTime, theme, member)),
                entityManager.persist(
                        Reservation.createWithoutIdAndPaymentHistory(TODAY, reservationTime, theme, member)));
        List<Waiting> waitings = List.of(
                entityManager.persist(Waiting.createWithoutIdWithoutPayment(TODAY, theme, reservationTime, member)),
                entityManager.persist(Waiting.createWithoutIdWithoutPayment(TODAY, theme, reservationTime, member)),
                entityManager.persist(Waiting.createWithoutIdWithoutPayment(TODAY, theme, reservationTime, member)));

        entityManager.flush();
        entityManager.clear();

        // when
        ReservationStatusResponse allReservationState =
                reservationQueryService.findAllReservationStatusByMember(member.getId());

        // then
        List<Long> reservationIds = reservations.stream().map(Reservation::getId).toList();
        List<Long> waitingIds = waitings.stream().map(Waiting::getId).toList();
        assertAll(
                () -> assertThat(allReservationState.reservationResponses())
                        .extracting(MineReservationResponse::getId)
                        .containsExactlyElementsOf(reservationIds),
                () -> assertThat(allReservationState.waitingWithRankResponses())
                        .extracting(WaitingWithRankResponse::id)
                        .containsExactlyElementsOf(waitingIds)
        );
    }

    @Nested
    @DisplayName("필터를 통해 예약을 조회할 수 있다")
    class findReservationsByFilter {

        @Test
        @DisplayName("필터 조건으로 특정 유저의 예약을 조회할 수 있다")
        void canFindReservationsByMemberFilter() {
            // given
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, reservationTime, theme, member));

            Member otherMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "다른회원", "otherMember@test.com", "password123!"));

            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, reservationTime, theme, otherMember));
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    NEXT_DAY, reservationTime, theme, otherMember));

            entityManager.flush();
            entityManager.clear();

            // when
            List<FindReservationsByFilter> reservations = reservationQueryService.findReservationsByFilter(
                    otherMember.getId(), theme.getId(), TODAY, TODAY.plusDays(7));

            // then
            assertThat(reservations).hasSize(2);
        }

        @Test
        @DisplayName("필터 조건으로 특정 테마의 예약을 조회할 수 있다")
        void canFindReservationsByThemeFilter() {
            // given
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, reservationTime, theme, member));

            Theme otherTheme = entityManager.persist(
                    Theme.createWithoutId("다른테마", "설명", "thumbnail.jpg"));

            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, reservationTime, otherTheme, member));
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    NEXT_DAY, reservationTime, otherTheme, member));

            entityManager.flush();
            entityManager.clear();

            // when
            List<FindReservationsByFilter> reservations = reservationQueryService.findReservationsByFilter(
                    member.getId(), otherTheme.getId(), TODAY, TODAY.plusDays(7));

            // then
            assertThat(reservations).hasSize(2);
        }

        @Test
        @DisplayName("필터 조건으로 특정 기간의 예약을 조회할 수 있다")
        void canFindReservationsByDateFilter() {
            // given
            entityManager.persist(
                    Reservation.createWithoutIdAndPaymentHistory(YESTERDAY, reservationTime, theme, member));
            entityManager.persist(
                    Reservation.createWithoutIdAndPaymentHistory(TODAY, reservationTime, theme, member));
            entityManager.persist(
                    Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, reservationTime, theme, member));

            entityManager.flush();
            entityManager.clear();

            // when
            List<Reservation> reservations = reservationRepository.findReservationsByFilter(
                    member.getId(), theme.getId(), TODAY, NEXT_DAY);

            // then
            assertAll(
                    () -> assertThat(reservations).hasSize(2),
                    () -> assertThat(reservations)
                            .extracting(Reservation::getDate)
                            .containsExactlyInAnyOrder(TODAY, NEXT_DAY)
            );
        }
    }
}
