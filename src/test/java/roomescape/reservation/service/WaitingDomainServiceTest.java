package roomescape.reservation.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import roomescape.auth.login.presentation.dto.LoginMemberInfo;
import roomescape.common.util.time.DateTime;
import roomescape.member.domain.MemberRepository;
import roomescape.member.infrastructure.JpaMemberRepository;
import roomescape.member.infrastructure.JpaMemberRepositoryAdapter;
import roomescape.member.presentation.dto.MemberResponse;
import roomescape.member.presentation.dto.MyReservationResponse;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.WaitingRepository;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.infrastructure.JpaReservationRepository;
import roomescape.reservation.infrastructure.JpaReservationRepositoryAdapter;
import roomescape.reservation.infrastructure.JpaWaitingRepository;
import roomescape.reservation.infrastructure.JpaWaitingRepositoryAdapter;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.dto.ReservationResponse;
import roomescape.reservation.presentation.dto.WaitingResponse;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.presentation.dto.ReservationTimeResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.presentation.dto.ThemeResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.reservation.service.WaitingDomainServiceTest.*;


@DataJpaTest
@Import(WaitingDomainConfig.class)
class WaitingDomainServiceTest {

    private static final String paymentKey = "tgen_20240513184816ZSAZ9";
    private static final String orderId = "MC4wNDYzMzA0OTc2MDgy";
    private static final int amount = 1000;

    @Autowired
    private WaitingDomainService waitingDomainService;

    @DisplayName("지나간 날짜와 시간에 대한 예약대기를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource
    void cant_not_reserve_waiting_before_now(final LocalDate date, final Long timeId, final Long themeId, final Long memberId) {
        Assertions.assertThatThrownBy(
                () -> waitingDomainService.createWaiting(new ReservationRequest(date, timeId, themeId, paymentKey, orderId, amount), memberId))
            .isInstanceOf(ReservationException.class)
            .hasMessage("예약할 수 없는 날짜와 시간입니다.");
    }

    private static Stream<Arguments> cant_not_reserve_waiting_before_now() {
        return Stream.of(
            Arguments.of(LocalDate.of(2025, 4, 26), 1L, 3L, 1L),
            Arguments.of(LocalDate.of(2025, 4, 18), 1L, 2L, 1L)
        );
    }

    @DisplayName("예약한 멤버는 예약대기를 생성할 수 없다.")
    @Test
    void cant_reserve_waiting_by_reservation_owner() {
        Assertions.assertThatThrownBy(
                () -> waitingDomainService.createWaiting(
                    new ReservationRequest(LocalDate.of(2025, 4, 28), 1L, 1L, paymentKey, orderId, amount), 1L))
            .isInstanceOf(ReservationException.class)
            .hasMessage("예약자는 예약대기를 할 수 없습니다.");
    }

    @DisplayName("동일한 사용자가 중복 예약대기를 할 수 없다.")
    @Test
    void cant_reserve_waiting_by_duplicate_member() {
        Assertions.assertThatThrownBy(
                () -> waitingDomainService.createWaiting(
                    new ReservationRequest(LocalDate.of(2025, 4, 28), 1L, 1L, paymentKey, orderId, amount), 2L))
            .isInstanceOf(ReservationException.class)
            .hasMessage("이미 예약대기 중입니다.");
    }

    @DisplayName("예약 대기를 생성할 수 있다.")
    @Test
    void can_create_waiting() {
        ReservationRequest request = new ReservationRequest(LocalDate.of(2025, 4, 28), 2L, 1L, paymentKey, orderId, amount);
        Long memberId = 2L;

        WaitingResponse response = waitingDomainService.createWaiting(request, memberId);

        assertThat(response.theme()).isEqualTo("테마1");
        assertThat(response.date()).isEqualTo(LocalDate.of(2025, 4, 28));
        assertThat(response.startAt()).isEqualTo(LocalTime.of(11, 0));
    }

    @DisplayName("예약 대기를 삭제할 수 있다.")
    @Test
    void can_delete_waiting() {
        Long waitingId = 1L;

        waitingDomainService.deleteWaiting(waitingId);

        List<ReservationResponse> allWaitings = waitingDomainService.findAllWaitings();
        assertThat(allWaitings).containsExactly(
            new ReservationResponse(
                2L,
                LocalDate.of(2025, 4, 28),
                ReservationTimeResponse.from(ReservationTime.createWithId(1L, LocalTime.of(10, 0))),
                ThemeResponse.from(Theme.createWithId(1L, "테마1", "재밌음", "/image/default.jpg")),
                new MemberResponse(3L, "유저3")
            )
        );
    }

    @DisplayName("존재하지 않는 예약 대기는 삭제할 수 없다.")
    @Test
    void cannot_delete_non_existent_waiting() {
        Long nonExistentWaitingId = 999L;

        Assertions.assertThatThrownBy(() -> waitingDomainService.deleteWaiting(nonExistentWaitingId))
            .isInstanceOf(ReservationException.class)
            .hasMessage("예약 대기를 찾을 수 없습니다.");
    }

    @DisplayName("예약 대기 목록을 조회할 수 있다.")
    @Test
    void can_find_all_waitings() {
        List<ReservationResponse> waitings = waitingDomainService.findAllWaitings();
        ReservationResponse secondResponse = waitings.get(1);

        assertThat(waitings.size()).isEqualTo(2);
        assertThat(secondResponse.theme().id()).isEqualTo(1L);
        assertThat(secondResponse.date()).isEqualTo(LocalDate.of(2025,4, 28));
        assertThat(secondResponse.time().id()).isEqualTo(1L);
    }

    static class WaitingDomainConfig {

        @Bean
        public DateTime dateTime() {
            return () -> LocalDateTime.of(2025, 4, 28, 10, 0);
        }

        @Bean
        public ReservationRepository reservationRepository(JpaReservationRepository jpaReservationRepository) {
            return new JpaReservationRepositoryAdapter(jpaReservationRepository);
        }

        @Bean
        public MemberRepository memberRepository(JpaMemberRepository jpaMemberRepository) {
            return new JpaMemberRepositoryAdapter(jpaMemberRepository);
        }

        @Bean
        public WaitingRepository waitingRepository(JpaWaitingRepository jpaWaitingRepository) {
            return new JpaWaitingRepositoryAdapter(jpaWaitingRepository);
        }

        @Bean
        public WaitingDomainService waitingDomainService(
            DateTime dateTime,
            ReservationRepository reservationRepository,
            MemberRepository memberRepository,
            WaitingRepository waitingRepository
        ) {
            return new WaitingDomainService(
                dateTime,
                reservationRepository,
                memberRepository,
                waitingRepository
            );
        }
    }
}