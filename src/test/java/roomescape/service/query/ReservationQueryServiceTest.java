package roomescape.service.query;

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
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.dto.response.MemberProfileResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationStatusResponse;
import roomescape.dto.response.ThemeResponse;
import roomescape.dto.response.WaitingWithRankResponse;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

@DataJpaTest
class ReservationQueryServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WaitingRepository waitingRepository;

    private ReservationQueryService reservationQueryService;

    private ReservationTime reservationTime;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setup() {
        reservationQueryService = new ReservationQueryService(
                reservationRepository, memberRepository, waitingRepository);

        reservationTime = entityManager.persist(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        theme = entityManager.persist(
                Theme.createWithoutId("테마", "테마 설명", "thumbnail.jpg"));
        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));
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

        // when
        List<ReservationResponse> allReservations = reservationQueryService.findAllReservations();

        // then
        assertAll(
                () -> assertThat(allReservations).hasSize(3),
                () -> assertThat(allReservations)
                        .extracting(ReservationResponse::member)
                        .extracting(MemberProfileResponse::id)
                        .containsExactly(member.getId(), member.getId(), member.getId())
        );
    }

    @DisplayName("회원의 모든 예약을 조회할 수 있다.")
    @Test
    void testMethodNameHere() {
        // given
        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, reservationTime, theme, member));
        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, reservationTime, theme, member));
        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, reservationTime, theme, member));

        Member otherMember = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member2@test.com", "password123!"));

        entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                TODAY, reservationTime, theme, otherMember));

        entityManager.flush();

        // when
        List<ReservationResponse> allReservations = reservationQueryService.findAllReservationsByMember(member.getId());

        // then
        assertAll(
                () -> assertThat(allReservations).hasSize(3),
                () -> assertThat(allReservations)
                        .extracting(ReservationResponse::member)
                        .extracting(MemberProfileResponse::id)
                        .containsExactly(member.getId(), member.getId(), member.getId())
        );
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
                entityManager.persist(
                        Waiting.createWithoutIdWithoutPayment(TODAY, theme, reservationTime, member)),
                entityManager.persist(Waiting.createWithoutIdWithoutPayment(TODAY, theme, reservationTime, member)),
                entityManager.persist(Waiting.createWithoutIdWithoutPayment(TODAY, theme, reservationTime, member)));

        // when
        ReservationStatusResponse allReservationState =
                reservationQueryService.findAllReservationStatusByMember(member.getId());

        // then
        List<Long> reservationIds = reservations.stream().map(Reservation::getId).toList();
        List<Long> waitingIds = waitings.stream().map(Waiting::getId).toList();
        assertAll(
                () -> assertThat(allReservationState.reservationResponses())
                        .extracting(ReservationResponse::id)
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

            // when
            List<ReservationResponse> reservations = reservationQueryService.findReservationsByFilter(
                    otherMember.getId(), theme.getId(), TODAY, TODAY.plusDays(7));

            // then
            assertAll(
                    () -> assertThat(reservations).hasSize(2),
                    () -> assertThat(reservations)
                            .extracting(ReservationResponse::member)
                            .extracting(MemberProfileResponse::id)
                            .containsExactly(otherMember.getId(), otherMember.getId())
            );
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

            // when
            List<ReservationResponse> reservations = reservationQueryService.findReservationsByFilter(
                    member.getId(), otherTheme.getId(), TODAY, TODAY.plusDays(7));

            // then
            assertAll(
                    () -> assertThat(reservations).hasSize(2),
                    () -> assertThat(reservations)
                            .extracting(ReservationResponse::theme)
                            .extracting(ThemeResponse::id)
                            .containsExactly(otherTheme.getId(), otherTheme.getId())
            );
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
