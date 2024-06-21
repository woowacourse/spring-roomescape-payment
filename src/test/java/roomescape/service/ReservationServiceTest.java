package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static roomescape.fixture.MemberFixture.memberFixture;
import static roomescape.fixture.ReservationFixture.RESERVATIONS;
import static roomescape.fixture.ReservationFixture.reservationFixture;
import static roomescape.fixture.TestFixture.ADMIN;
import static roomescape.fixture.TestFixture.ADMIN_NAME;
import static roomescape.fixture.TestFixture.AMOUNT;
import static roomescape.fixture.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.fixture.TestFixture.DATE_MAY_NINTH;
import static roomescape.fixture.TestFixture.MEMBER_TENNY;
import static roomescape.fixture.TestFixture.ORDER_ID;
import static roomescape.fixture.TestFixture.PAYMENT_KEY;
import static roomescape.fixture.TestFixture.RESERVATION_TIME_SEVEN;
import static roomescape.fixture.TestFixture.RESERVATION_TIME_SIX;
import static roomescape.fixture.TestFixture.START_AT_SEVEN;
import static roomescape.fixture.TestFixture.START_AT_SIX;
import static roomescape.fixture.TestFixture.THEME_DETECTIVE;
import static roomescape.fixture.TestFixture.THEME_DETECTIVE_NAME;
import static roomescape.fixture.TestFixture.THEME_HORROR;
import static roomescape.fixture.TestFixture.THEME_HORROR_NAME;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.ReservationFilterParam;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.dto.reservation.ReservationWithPaymentRequest;
import roomescape.dto.theme.ReservedThemeResponse;
import roomescape.exception.RoomescapeException;
import roomescape.fixture.TestFixture;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReservationTimeRepository reservationTimeRepository;

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("예약을 생성한다.")
    void create() {
        // given
        var reservation = reservationFixture(1L);
        var member = reservation.getMember();
        var time = reservation.getTime();
        var theme = reservation.getTheme();
        var payment = reservation.getPayment();
        var request = new ReservationWithPaymentRequest(
                reservation.getDate(),
                time.getId(),
                theme.getId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount()
        );

        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(reservation.getMember()));
        given(reservationTimeRepository.findById(time.getId()))
                .willReturn(Optional.of(time));
        given(themeRepository.findById(theme.getId()))
                .willReturn(Optional.of(theme));
        given(paymentRepository.findByPaymentKey(payment.getPaymentKey()))
                .willReturn(Optional.of(payment));
        given(reservationRepository.save(any()))
                .willReturn(reservation);

        // when
        ReservationResponse response = reservationService.createReservation(request, member.getId());

        // then
        assertThat(response).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 0})
    @DisplayName("이전 날짜 혹은 당일 예약을 할 경우 예외가 발생한다.")
    void throwExceptionWhenCreateReservationAtInvalidDate(final int days) {
        final LocalDate date = LocalDate.now().minusDays(days);
        final ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(date, 1L, 1L, PAYMENT_KEY,
                ORDER_ID, AMOUNT);

        assertThatThrownBy(() -> reservationService.createReservation(request, 1L))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("동일한 테마, 날짜, 시간에 예약이 초과된 경우 예외가 발생한다.")
    void throwExceptionWhenCreateDuplicatedReservation() {
        // given
        var reservation = reservationFixture(1L);
        var member = reservation.getMember();
        var time = reservation.getTime();
        var theme = reservation.getTheme();
        var payment = reservation.getPayment();

        var request = new ReservationWithPaymentRequest(
                reservation.getDate(),
                time.getId(),
                theme.getId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount()
        );

        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(member));
        given(reservationTimeRepository.findById(time.getId()))
                .willReturn(Optional.of(time));
        given(themeRepository.findById(theme.getId()))
                .willReturn(Optional.of(theme));
        given(paymentRepository.findByPaymentKey(payment.getPaymentKey()))
                .willReturn(Optional.of(payment));
        given(reservationRepository.countByDateAndTimeAndTheme(reservation.getDate(), time, theme))
                .willReturn(1);

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(request, member.getId()))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("모든 예약 목록을 조회한다.")
    void findAllReservations() {
        // given
        final Reservation reservation1 = Reservation.builder()
                .id(1L)
                .member(MEMBER_TENNY())
                .date(DATE_MAY_EIGHTH)
                .time(RESERVATION_TIME_SIX())
                .theme(THEME_HORROR())
                .status(ReservationStatus.RESERVED)
                .build();
        final Reservation reservation2 = Reservation.builder()
                .id(1L)
                .member(ADMIN())
                .date(DATE_MAY_EIGHTH)
                .time(RESERVATION_TIME_SEVEN())
                .theme(THEME_DETECTIVE())
                .status(ReservationStatus.RESERVED)
                .build();
        given(reservationRepository.findAll())
                .willReturn(List.of(reservation1, reservation2));

        // when
        final List<ReservationResponse> reservations = reservationService.findAll();

        // then
        assertAll(() -> {
            assertThat(reservations).hasSize(2)
                    .extracting(ReservationResponse::name)
                    .containsExactly(TestFixture.MEMBER_TENNY_NAME, ADMIN_NAME);
            assertThat(reservations).extracting(ReservationResponse::date)
                    .containsExactly(DATE_MAY_EIGHTH, DATE_MAY_EIGHTH);
            assertThat(reservations).extracting(ReservationResponse::time)
                    .extracting(ReservationTimeResponse::startAt)
                    .containsExactly(START_AT_SIX, START_AT_SEVEN);
            assertThat(reservations).extracting(ReservationResponse::theme)
                    .extracting(ReservedThemeResponse::name)
                    .containsExactly(THEME_HORROR_NAME, THEME_DETECTIVE_NAME);
        });
    }

    @Test
    @DisplayName("검색 조건에 따른 예약 목록을 조회한다.")
    void findAllByFilterParameter() {
        // given
        final Reservation reservation1 = Reservation.builder()
                .member(MEMBER_TENNY())
                .date(DATE_MAY_EIGHTH)
                .time(RESERVATION_TIME_SIX())
                .theme(THEME_HORROR())
                .status(ReservationStatus.RESERVED)
                .build();
        final Reservation reservation2 = Reservation.builder()
                .member(MEMBER_TENNY())
                .date(DATE_MAY_NINTH)
                .time(RESERVATION_TIME_SIX())
                .theme(THEME_HORROR())
                .status(ReservationStatus.RESERVED)
                .build();
        final ReservationFilterParam reservationFilterParam
                = new ReservationFilterParam(1L, 1L,
                LocalDate.parse("2034-05-08"), LocalDate.parse("2034-05-28"));
        given(reservationRepository.findByThemeIdAndMemberIdAndDateBetweenAndStatus(1L, 1L,
                LocalDate.parse("2034-05-08"), LocalDate.parse("2034-05-28"), ReservationStatus.RESERVED))
                .willReturn(List.of(reservation1, reservation2));

        // when
        final List<ReservationResponse> reservations
                = reservationService.findAllBy(reservationFilterParam);

        // then
        assertAll(() -> {
            assertThat(reservations).hasSize(2)
                    .extracting(ReservationResponse::name)
                    .containsExactly(TestFixture.MEMBER_TENNY_NAME, TestFixture.MEMBER_TENNY_NAME);
            assertThat(reservations).extracting(ReservationResponse::time)
                    .extracting(ReservationTimeResponse::startAt)
                    .containsExactly(START_AT_SIX, START_AT_SIX);
            assertThat(reservations).extracting(ReservationResponse::theme)
                    .extracting(ReservedThemeResponse::name)
                    .containsExactly(THEME_HORROR_NAME, THEME_HORROR_NAME);
        });
    }

    @Test
    @DisplayName("예약을 삭제한다.")
    void delete() {
        // given
        final Long existingId = 1L;
        given(reservationRepository.existsById(existingId)).willReturn(true);

        // when & then
        assertThatCode(() -> reservationService.delete(existingId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("삭제하려는 예약이 존재하지 않는 경우 예외가 발생한다.")
    void throwExceptionWhenDeleteNotExistingReservation() {
        // given
        final Long notExistingId = 1L;
        given(reservationRepository.existsById(notExistingId)).willThrow(RoomescapeException.class);

        // when & then
        assertThatThrownBy(() -> reservationService.delete(notExistingId))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("특정 사용자의 예약 및 예약 대기 목록을 조회한다.")
    void findMyReservations() {
        // given
        var member = memberFixture(1L);
        var loginMember = new LoginMember(member.getId(), member.getNameString(), member.getEmail(), member.getRole());

        given(reservationRepository.findByMemberId(member.getId()))
                .willReturn(List.of(reservationFixture(1L)));
        given(reservationRepository.findAll())
                .willReturn(RESERVATIONS);

        // when
        final var actual = reservationService.findMyReservationsAndWaitings(loginMember);

        // then
        assertThat(actual).hasSize(1);
    }
}
