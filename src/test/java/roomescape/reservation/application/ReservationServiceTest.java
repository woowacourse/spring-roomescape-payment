package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import roomescape.auth.dto.info.LoginMemberInfo;
import roomescape.common.util.time.DateTime;
import roomescape.member.domain.MemberRepository;
import roomescape.member.infrastructure.MemberJpaRepository;
import roomescape.member.infrastructure.MemberJpaRepositoryAdapter;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.infrastructure.PaymentJpaRepository;
import roomescape.payment.infrastructure.PaymentJpaRepositoryAdapter;
import roomescape.payment.infrastructure.dto.request.TossPaymentRequest;
import roomescape.payment.infrastructure.dto.response.TossPaymentResponse;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.WaitingRepository;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationMineResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.infrastructure.ReservationJpaRepository;
import roomescape.reservation.infrastructure.ReservationJpaRepositoryAdapter;
import roomescape.reservation.infrastructure.WaitingJpaRepository;
import roomescape.reservation.infrastructure.WaitingJpaRepositoryAdapter;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.infrastructure.ThemeJpaRepository;
import roomescape.theme.infrastructure.ThemeJpaRepositoryAdapter;
import roomescape.timeslot.domain.TimeSlotRepository;
import roomescape.timeslot.infrastructure.TimeSlotJpaRepository;
import roomescape.timeslot.infrastructure.TimeSlotJpaRepositoryAdapter;

@DataJpaTest
@Import(ReservationServiceTest.ReservationConfig.class)
class ReservationServiceTest {

    private static final String PAYMENT_KEY = "tgen_20240513184816ZSAZ9";
    private static final String ORDER_ID = "MC4wNDYzMzA0OTc2MDgy";
    private static final int AMOUNT = 1000;
    private static final LocalDate CURRENT_DATE = LocalDate.of(2025, 4, 28);

    @Autowired
    private ReservationService reservationService;
    ;

    @Autowired
    private PaymentClient paymentClient;

    private ReservationWithPaymentRequest createRequestWithPayment(LocalDate date, Long timeId, Long themeId,
                                                                   String paymentKey, String orderId, int amount) {
        return new ReservationWithPaymentRequest(date, timeId, themeId, paymentKey, orderId, amount);
    }

    private ReservationRequest createRequest(LocalDate date, Long timeId, Long themeId) {
        return new ReservationRequest(date, timeId, themeId);
    }

    private void assertReservationException(ThrowingCallable action, String message) {
        assertThatThrownBy(action)
                .isInstanceOf(ReservationException.class)
                .hasMessage(message);
    }

    @Nested
    @DisplayName("예약 생성")
    class CreateReservation {

        @Test
        @DisplayName("결제가 있는 정상적인 예약을 생성할 수 있다.")
        void createReservationWithPayment() {
            // given
            ReservationWithPaymentRequest request = createRequestWithPayment(CURRENT_DATE.plusDays(1), 2L, 1L,
                    PAYMENT_KEY, ORDER_ID, AMOUNT);
            Long memberId = 1L;

            when(paymentClient.requestPayment(any(TossPaymentRequest.class)))
                    .thenReturn(
                            new TossPaymentResponse(PAYMENT_KEY, ORDER_ID, AMOUNT, LocalDateTime.of(2025, 6, 1, 10, 0).atOffset(
                                    ZoneOffset.ofHours(9))));

            // when & then
            assertThatCode(() -> reservationService.createReservationWithPayment(request, memberId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("결제가 없는 정상적인 예약을 생성할 수 있다.")
        void createReservationWithoutPayment() {
            // given
            ReservationRequest request = createRequest(CURRENT_DATE.plusDays(1), 2L, 1L);
            Long memberId = 1L;

            // when & then
            assertThatCode(() -> reservationService.createReservationWithoutPayment(request, memberId))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @MethodSource("roomescape.reservation.application.ReservationServiceTest#invalidPastDates")
        @DisplayName("과거 시간을 예약하면 예외가 발생한다.")
        void cannotReserveInPast(LocalDate date, Long timeId) {
            // given
            ReservationRequest request = createRequest(date, timeId, 1L);
            Long memberId = 1L;

            // when & then
            assertReservationException(() -> reservationService.createReservationWithoutPayment(request, memberId),
                    "예약할 수 없는 날짜와 시간입니다.");
        }

        @Test
        @DisplayName("중복된 예약을 하면 예외가 발생한다.")
        void cannotReserveDuplicate() {
            // given
            ReservationRequest request = createRequest(CURRENT_DATE, 1L, 1L);
            Long memberId = 1L;

            // when & then
            assertReservationException(() -> reservationService.createReservationWithoutPayment(request, memberId),
                    "이미 예약이 존재합니다.");
        }
    }

    @Nested
    @DisplayName("예약 대기")
    class Waiting {

        @Test
        @DisplayName("정상적인 예약 대기를 생성할 수 있다.")
        void canCreateWaiting() {
            // given
            ReservationRequest request = createRequest(CURRENT_DATE, 2L, 1L);
            Long memberId = 2L;

            // when
            WaitingResponse response = reservationService.createWaiting(request, memberId);

            // then
            assertAll(
                    () -> assertThat(response.theme()).isEqualTo("테마1"),
                    () -> assertThat(response.date()).isEqualTo(CURRENT_DATE),
                    () -> assertThat(response.startAt()).isEqualTo(LocalTime.of(11, 0))
            );
        }

        @Test
        @DisplayName("예약 대기를 삭제할 수 있다.")
        void deleteWaiting() {
            // given & when
            reservationService.deleteWaiting(1L);
            List<ReservationMineResponse> responses = reservationService.getMemberReservations(new LoginMemberInfo(2L));

            // then
            assertThat(responses).extracting(ReservationMineResponse::theme)
                    .containsOnly("테마3");
        }

        @ParameterizedTest
        @MethodSource("roomescape.reservation.application.ReservationServiceTest#invalidWaitings")
        @DisplayName("과거 시간을 예약 대기하면 예외가 발생한다.")
        void cannotCreateWaitingInPast(LocalDate date, Long timeId, Long themeId, Long memberId) {
            assertReservationException(
                    () -> reservationService.createWaiting(createRequest(date, timeId, themeId), memberId),
                    "예약할 수 없는 날짜와 시간입니다.");
        }

        @Test
        @DisplayName("예약자는 예약 대기를 하면 예외가 발생한다.")
        void reservationOwnerCannotCreateWaiting() {
            // given
            ReservationRequest request = createRequest(CURRENT_DATE, 1L, 1L);
            Long memberId = 1L;

            // when & then
            assertReservationException(() -> reservationService.createWaiting(request, memberId),
                    "예약자는 예약대기를 할 수 없습니다.");
        }

        @Test
        @DisplayName("중복된 예약 대기를 하면 예외가 발생한다.")
        void duplicateWaitingNotAllowed() {
            // given
            ReservationRequest request = createRequest(CURRENT_DATE, 1L, 1L);
            Long memberId = 2L;

            // when & then
            assertReservationException(() -> reservationService.createWaiting(request, memberId),
                    "이미 예약대기 중입니다.");
        }

        @Test
        @DisplayName("존재하지 않는 예약 대기를 삭제하면 예외가 발생한다.")
        void deleteNonExistentWaiting() {
            assertReservationException(() -> reservationService.deleteWaiting(999L), "예약 대기를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("예약 삭제")
    class DeleteReservation {

        @Test
        @DisplayName("예약 삭제 시 예약 대기가 예약으로 승인된다.")
        void firstWaitingGetsReservationOnDelete() {
            // given & when
            reservationService.deleteReservationById(1L);

            // then
            List<ReservationMineResponse> memberReservations = reservationService.getMemberReservations(
                    new LoginMemberInfo(2L));
            assertThat(memberReservations).anyMatch(res -> res.status().equals("예약"));
        }

        @Test
        @DisplayName("대기 없는 예약을 삭제 할 수 있다.")
        void canDeleteReservationWithoutWaiting() {
            // given & when
            reservationService.deleteReservationById(2L);

            // then
            List<ReservationResponse> reservations = reservationService.getReservations();
            assertThat(reservations).noneMatch(res -> res.id().equals(2L));
        }

        @Test
        @DisplayName("존재하지 않는 예약을 삭제하면 예외가 발생한다.")
        void cannotDeleteNonExistentReservation() {
            assertReservationException(() -> reservationService.deleteReservationById(999L), "예약을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("예약 조회")
    class FindReservation {

        @Test
        @DisplayName("예약 및 예약 대기를 조회할 수 있다.")
        void findMyReservations() {
            // given & when
            List<ReservationMineResponse> results = reservationService.getMemberReservations(new LoginMemberInfo(2L));

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("모든 예약 대기를 조회할 수 있다.")
        void findAllWaitings() {
            // given & when
            List<ReservationResponse> waitings = reservationService.findAllWaitings();

            // then
            assertThat(waitings).hasSize(2);
        }
    }

    private static Stream<Arguments> invalidPastDates() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 10, 5), 1L),
                Arguments.of(LocalDate.of(2024, 9, 5), 1L)
        );
    }

    private static Stream<Arguments> invalidWaitings() {
        return Stream.of(
                Arguments.of(LocalDate.of(2025, 4, 26), 1L, 3L, 1L),
                Arguments.of(LocalDate.of(2025, 4, 18), 1L, 2L, 1L)
        );
    }

    static class ReservationConfig {
        @Bean
        @Primary
        DateTime dateTime() {
            return () -> LocalDateTime.of(CURRENT_DATE, LocalTime.of(10, 0));
        }

        @Bean
        @Primary
        ReservationRepository reservationRepository(ReservationJpaRepository repo) {
            return new ReservationJpaRepositoryAdapter(repo);
        }

        @Bean
        @Primary
        TimeSlotRepository timeSlotRepository(TimeSlotJpaRepository repo) {
            return new TimeSlotJpaRepositoryAdapter(repo);
        }

        @Bean
        @Primary
        ThemeRepository themeRepository(ThemeJpaRepository repo) {
            return new ThemeJpaRepositoryAdapter(repo);
        }

        @Bean
        @Primary
        MemberRepository memberRepository(MemberJpaRepository repo) {
            return new MemberJpaRepositoryAdapter(repo);
        }

        @Bean
        @Primary
        WaitingRepository waitingRepository(WaitingJpaRepository repo) {
            return new WaitingJpaRepositoryAdapter(repo);
        }

        @Bean
        @Primary
        PaymentRepository paymentRepository(PaymentJpaRepository repo) {
            return new PaymentJpaRepositoryAdapter(repo);
        }

        @Bean
        @Primary
        PaymentClient paymentClient() {
            return mock(PaymentClient.class);
        }

        @Bean
        PaymentService paymentService(PaymentClient client, PaymentRepository repo) {
            return new PaymentService(client, repo);
        }

        @Bean
        ReservationService reservationService(DateTime dt, ReservationRepository rr, TimeSlotRepository tr,
                                              ThemeRepository thr, MemberRepository mr, WaitingRepository wr,
                                              PaymentService ps) {
            return new ReservationService(dt, rr, tr, thr, mr, wr, ps);
        }
    }
}
