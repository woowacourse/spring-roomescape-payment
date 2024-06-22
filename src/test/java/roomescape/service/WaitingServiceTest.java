package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static roomescape.fixture.ReservationFixture.reservationFixture;
import static roomescape.fixture.TestFixture.AMOUNT;
import static roomescape.fixture.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.fixture.TestFixture.MEMBER_TENNY;
import static roomescape.fixture.TestFixture.ORDER_ID;
import static roomescape.fixture.TestFixture.PAYMENT_KEY;
import static roomescape.fixture.TestFixture.RESERVATION_TIME_SIX;
import static roomescape.fixture.TestFixture.THEME_HORROR;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationWaitingRequest;
import roomescape.dto.reservation.ReservationWithPaymentRequest;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@ExtendWith(MockitoExtension.class)
class WaitingServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReservationTimeRepository reservationTimeRepository;

    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private WaitingService waitingService;

    @Test
    @DisplayName("예약 대기를 등록한다.")
    void createReservationWaiting() {
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final Reservation waiting = Reservation.builder()
                .id(2L)
                .member(member)
                .date(date)
                .time(time)
                .theme(theme)
                .status(ReservationStatus.PENDING)
                .build();
        final ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(date, 1L, 1L, PAYMENT_KEY,
                ORDER_ID, AMOUNT);
        final ReservationWaitingRequest reservationDto = new ReservationWaitingRequest(request, 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reservationTimeRepository.findById(1L)).willReturn(Optional.of(time));
        given(themeRepository.findById(1L)).willReturn(Optional.of(theme));
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatus(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED))
                .willReturn(true);
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatusAndMember(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED,
                waiting.getMember()))
                .willReturn(false);
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatusAndMember(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.PENDING,
                waiting.getMember()))
                .willReturn(false);
        given(reservationRepository.save(any())).willReturn(waiting);

        final ReservationResponse actual = waitingService.createReservationWaiting(reservationDto);

        assertThat(actual.id()).isNotNull();
    }

    @Test
    @DisplayName("사용자가 예약이 없는 건에 대해 예약 대기를 등록하려는 경우 예외가 발생한다.")
    void throwExceptionWhenDoesNotExistReservation() {
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final Reservation waiting = Reservation.builder()
                .id(2L)
                .member(member)
                .date(date)
                .time(time)
                .theme(theme)
                .status(ReservationStatus.PENDING)
                .build();
        final ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(date, 1L, 1L, PAYMENT_KEY,
                ORDER_ID, AMOUNT);
        final ReservationWaitingRequest reservationDto = new ReservationWaitingRequest(request, 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reservationTimeRepository.findById(1L)).willReturn(Optional.of(time));
        given(themeRepository.findById(1L)).willReturn(Optional.of(theme));
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatus(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED
        )).willReturn(false);

        assertThatThrownBy(() -> waitingService.createReservationWaiting(reservationDto))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("사용자가 이미 예약한 건에 대해 예약 대기를 등록하려는 경우 예외가 발생한다.")
    void throwExceptionWhenAlreadyReserved() {
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final Reservation waiting = Reservation.builder()
                .id(2L)
                .member(member)
                .date(date)
                .time(time)
                .theme(theme)
                .status(ReservationStatus.PENDING)
                .build();
        final ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(date, 1L, 1L, PAYMENT_KEY,
                ORDER_ID, AMOUNT);
        final ReservationWaitingRequest reservationDto = new ReservationWaitingRequest(request, 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reservationTimeRepository.findById(1L)).willReturn(Optional.of(time));
        given(themeRepository.findById(1L)).willReturn(Optional.of(theme));
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatus(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED
        )).willReturn(true);
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatusAndMember(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED,
                waiting.getMember()
        )).willReturn(true);

        assertThatThrownBy(() -> waitingService.createReservationWaiting(reservationDto))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("사용자가 중복해서 예약 대기를 등록하려는 경우 예외가 발생한다. ")
    void throwExceptionWhenDuplicatedWaiting() {
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final Reservation waiting = Reservation.builder()
                .id(2L)
                .member(member)
                .date(date)
                .time(time)
                .theme(theme)
                .status(ReservationStatus.PENDING)
                .build();
        final ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(date, 1L, 1L, PAYMENT_KEY,
                ORDER_ID, AMOUNT);
        final ReservationWaitingRequest reservationDto = new ReservationWaitingRequest(request, 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reservationTimeRepository.findById(1L)).willReturn(Optional.of(time));
        given(themeRepository.findById(1L)).willReturn(Optional.of(theme));
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatus(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED
        )).willReturn(true);
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatusAndMember(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED,
                waiting.getMember()
        )).willReturn(false);
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatusAndMember(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.PENDING, waiting.getMember()
        )).willReturn(true);

        assertThatThrownBy(() -> waitingService.createReservationWaiting(reservationDto))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("예약 대기 목록을 조회한다.")
    void findReservationWaitings() {
        // given
        final Reservation reservation = reservationFixture(1);
        given(reservationRepository.findByStatus(ReservationStatus.PENDING))
                .willReturn(List.of(reservation));

        // when
        final List<ReservationResponse> actual = waitingService.findReservationWaitings();

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("예약 대기를 승인한다.")
    void approveReservationWaiting() {
        // given
        final Reservation waiting = reservationFixture(1);
        given(reservationRepository.findById(waiting.getId())).willReturn(Optional.of(waiting));
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatus(waiting.getTheme(), waiting.getDate(),
                waiting.getTime(), ReservationStatus.RESERVED))
                .willReturn(false);

        // when
        waitingService.approveReservationWaiting(waiting.getId());

        // then
        assertThat(waiting.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @Test
    @DisplayName("이미 예약이 있는 상태에서 승인을 할 경우 예외가 발생한다.")
    void throwExceptionWhenAlreadyExistsReservation() {
        // given
        final Reservation waiting = reservationFixture(1);
        given(reservationRepository.findById(waiting.getId())).willReturn(Optional.of(waiting));
        given(reservationRepository.existsByThemeAndDateAndTimeAndStatus(waiting.getTheme(), waiting.getDate(),
                waiting.getTime(), ReservationStatus.RESERVED))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> waitingService.approveReservationWaiting(waiting.getId()))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("예약 대기를 거절한다.")
    void rejectReservationWaiting() {
        // given
        final Reservation waiting = reservationFixture(1);
        given(reservationRepository.existsById(waiting.getId()))
                .willReturn(true);

        // when & then
        assertThatCode(() -> waitingService.rejectReservationWaiting(waiting.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Id에 해당하는 예약 대기가 없으면 예외가 발생한다.")
    void throwExceptionWhenRejectNotExistingReservationWaiting() {
        // given
        final Long notExistingId = 0L;
        given(reservationRepository.existsById(notExistingId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> waitingService.rejectReservationWaiting(notExistingId))
                .isInstanceOf(RoomescapeException.class);
    }
}
