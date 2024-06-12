package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import static roomescape.fixture.TestFixture.ADMIN;
import static roomescape.fixture.TestFixture.ADMIN_NAME;
import static roomescape.fixture.TestFixture.AMOUNT;
import static roomescape.fixture.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.fixture.TestFixture.DATE_MAY_NINTH;
import static roomescape.fixture.TestFixture.MEMBER_MIA;
import static roomescape.fixture.TestFixture.MEMBER_TENNY;
import static roomescape.fixture.TestFixture.MEMBER_TENNY_EMAIL;
import static roomescape.fixture.TestFixture.MEMBER_TENNY_NAME;
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

import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.MyReservationWithRankResponse;
import roomescape.dto.reservation.ReservationDto;
import roomescape.dto.reservation.ReservationFilterParam;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.dto.reservation.ReservationTimeResponse;
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
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final Reservation reservation = new Reservation(member, date, time, theme, ReservationStatus.RESERVED);
        final ReservationSaveRequest request = new ReservationSaveRequest(date, 1L, 1L, PAYMENT_KEY, ORDER_ID, AMOUNT);
        final ReservationDto reservationDto = ReservationDto.of(request, 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reservationTimeRepository.findById(1L)).willReturn(Optional.of(time));
        given(themeRepository.findById(1L)).willReturn(Optional.of(theme));
        given(reservationRepository.save(reservation))
                .willReturn(new Reservation(1L, reservation.getMember(), reservation.getDate(),
                        reservation.getTime(), reservation.getTheme(), ReservationStatus.RESERVED));

        // when
        final ReservationResponse response = reservationService.createReservation(reservationDto);

        // then
        assertThat(response).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 0})
    @DisplayName("이전 날짜 혹은 당일 예약을 할 경우 예외가 발생한다.")
    void throwExceptionWhenCreateReservationAtInvalidDate(final int days) {
        final LocalDate date = LocalDate.now().minusDays(days);
        final ReservationSaveRequest request = new ReservationSaveRequest(date, 1L, 1L, PAYMENT_KEY, ORDER_ID, AMOUNT);
        final ReservationDto reservationDto = ReservationDto.of(request, 1L);

        assertThatThrownBy(() -> reservationService.createReservation(reservationDto))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("동일한 테마, 날짜, 시간에 예약이 초과된 경우 예외가 발생한다.")
    void throwExceptionWhenCreateDuplicatedReservation() {
        // given
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final ReservationSaveRequest request = new ReservationSaveRequest(DATE_MAY_EIGHTH, time.getId(), theme.getId(),
                PAYMENT_KEY, ORDER_ID, AMOUNT);
        final ReservationDto reservationDto = ReservationDto.of(request, member.getId());
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reservationTimeRepository.findById(1L)).willReturn(Optional.of(time));
        given(themeRepository.findById(1L)).willReturn(Optional.of(theme));
        given(reservationRepository.countByDateAndTimeIdAndThemeId(date, time.getId(), theme.getId()))
                .willReturn(1);

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(reservationDto))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("모든 예약 목록을 조회한다.")
    void findAllReservations() {
        // given
        final Reservation reservation1 = new Reservation(TestFixture.MEMBER_TENNY(), DATE_MAY_EIGHTH,
                RESERVATION_TIME_SIX(), THEME_HORROR(), ReservationStatus.RESERVED);
        final Reservation reservation2 = new Reservation(ADMIN(), DATE_MAY_EIGHTH,
                RESERVATION_TIME_SEVEN(), THEME_DETECTIVE(), ReservationStatus.RESERVED);
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
        final Reservation reservation1 = new Reservation(TestFixture.MEMBER_TENNY(), DATE_MAY_EIGHTH,
                RESERVATION_TIME_SIX(), THEME_HORROR(), ReservationStatus.RESERVED);
        final Reservation reservation2 = new Reservation(TestFixture.MEMBER_TENNY(), DATE_MAY_NINTH,
                RESERVATION_TIME_SIX(), THEME_HORROR(), ReservationStatus.RESERVED);
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
        final LoginMember loginMember = new LoginMember(1L, MEMBER_TENNY_NAME, MEMBER_TENNY_EMAIL, Role.MEMBER);
        final Reservation memberReservation = new Reservation(1L, MEMBER_TENNY(), DATE_MAY_EIGHTH,
                RESERVATION_TIME_SIX(), THEME_HORROR(), ReservationStatus.RESERVED);
        final Reservation reservation = new Reservation(2L, ADMIN(), DATE_MAY_NINTH,
                RESERVATION_TIME_SIX(), THEME_HORROR(), ReservationStatus.RESERVED);
        final Reservation waiting = new Reservation(3L, MEMBER_MIA(), DATE_MAY_NINTH,
                RESERVATION_TIME_SIX(), THEME_HORROR(), ReservationStatus.WAITING);
        final Reservation memberWaiting = new Reservation(4L, MEMBER_TENNY(), DATE_MAY_NINTH,
                RESERVATION_TIME_SIX(), THEME_HORROR(), ReservationStatus.WAITING);
        given(reservationRepository.findByMemberId(loginMember.id()))
                .willReturn(List.of(memberReservation, memberWaiting));
        given(reservationRepository.findAll())
                .willReturn(List.of(memberReservation, reservation, waiting, memberWaiting));
        given(paymentRepository.findByReservationId(1L))
                .willReturn(Optional.of(new Payment(1L, reservation, "paymentKey", "orderId", 1000L)));
        given(paymentRepository.findByReservationId(4L))
                .willReturn(Optional.of(new Payment(1L, reservation, "paymentKey", "orderId", 1000L)));

        // when
        final List<MyReservationWithRankResponse> actual = reservationService.findMyReservationsAndWaitings(
                loginMember);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actual.get(0).getStatus()).isEqualTo(ReservationStatus.RESERVED.value()),
                () -> assertThat(actual.get(0).getRank()).isEqualTo(1L),
                () -> assertThat(actual.get(1).getStatus()).isEqualTo(ReservationStatus.WAITING.value()),
                () -> assertThat(actual.get(1).getRank()).isEqualTo(2L)
        );
    }
}
