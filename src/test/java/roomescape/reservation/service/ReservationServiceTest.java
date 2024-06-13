package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static roomescape.member.fixture.MemberFixture.MEMBER_ID_1;
import static roomescape.payment.fixture.PaymentFixture.PAYMENT;
import static roomescape.reservation.fixture.ReservationFixture.PAST_RESERVATION_WITH_PAYMENT_REQUEST_1;
import static roomescape.reservation.fixture.ReservationFixture.RESERVATION_REQUEST_1;
import static roomescape.reservation.fixture.ReservationFixture.RESERVATION_WITH_PAYMENT_REQUEST_1;
import static roomescape.reservation.fixture.ReservationFixture.SAVED_RESERVATION_1;
import static roomescape.reservation.fixture.ReservationFixture.SAVED_RESERVATION_2;
import static roomescape.theme.fixture.ThemeFixture.THEME_1;
import static roomescape.time.fixture.DateTimeFixture.TOMORROW;
import static roomescape.time.fixture.ReservationTimeFixture.RESERVATION_TIME_10_00_ID_1;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.global.exception.DuplicateSaveException;
import roomescape.global.exception.IllegalReservationDateException;
import roomescape.global.exception.NoSuchRecordException;
import roomescape.member.domain.MemberRepository;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.ReservationTimeRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private PaymentService paymentService;

//    @Mock
//    private TossPaymentClient tossPaymentClient;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationTimeRepository reservationTimeRepository;

    @Mock
    private ThemeRepository themeRepository;


    @DisplayName("전체 예약을 조회하고 응답 형태로 반환할 수 있다")
    @Test
    void should_return_response_when_requested_all() {
        when(reservationRepository.findAll()).thenReturn(List.of(SAVED_RESERVATION_1, SAVED_RESERVATION_2));

        assertThat(reservationService.findAllReservation())
                .contains(new ReservationResponse(SAVED_RESERVATION_1), new ReservationResponse(SAVED_RESERVATION_2));
    }

    @DisplayName("예약을 추가하고 응답을 반환할 수 있다")
    @Test
    void should_save_reservation_when_requested() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(MEMBER_ID_1));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(SAVED_RESERVATION_1);
        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(RESERVATION_TIME_10_00_ID_1));
        when(themeRepository.findById(1L)).thenReturn(Optional.of(THEME_1));
        when(paymentService.confirmPayment(any(PaymentConfirmRequest.class), anyLong())).thenReturn(PAYMENT);

        ReservationResponse savedReservation = reservationService.saveMemberReservation(1L,
                RESERVATION_WITH_PAYMENT_REQUEST_1);

        assertThat(savedReservation).isEqualTo(new ReservationResponse(SAVED_RESERVATION_1));
    }

    @DisplayName("존재하지 않는 예약시각으로 예약 시 예외가 발생한다")
    @Test
    void should_throw_exception_when_request_with_non_exist_time() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(MEMBER_ID_1));
        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> reservationService.saveMemberReservation(1L, RESERVATION_WITH_PAYMENT_REQUEST_1))
                .isInstanceOf(NoSuchRecordException.class);
    }

    @DisplayName("존재하지 않은 테마로 예약 시 예외가 발생한다")
    @Test
    void should_throw_exception_when_request_with_non_exist_theme() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(MEMBER_ID_1));
        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(RESERVATION_TIME_10_00_ID_1));
        when(themeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> reservationService.saveMemberReservation(1L, RESERVATION_WITH_PAYMENT_REQUEST_1))
                .isInstanceOf(NoSuchRecordException.class);
    }

    @DisplayName("현재보다 이전날짜로 예약 시 예외가 발생한다")
    @Test
    void should_throw_exception_when_request_with_past_date() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(MEMBER_ID_1));
        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(RESERVATION_TIME_10_00_ID_1));

        assertThatThrownBy(
                () -> reservationService.saveMemberReservation(1L,
                        PAST_RESERVATION_WITH_PAYMENT_REQUEST_1))
                .isInstanceOf(IllegalReservationDateException.class);
    }

    @DisplayName("예약 날짜와 예약시각 그리고 테마 아이디가 같은 예약이 미리 존재하는 경우 예외가 발생한다")
    @Test
    void should_throw_exception_when_reserve_date_and_time_and_theme_duplicated() {
        when(reservationRepository.existsByDateValueAndTimeIdAndThemeId(TOMORROW, 1L, 1L)).thenReturn(true);

        assertThatThrownBy(
                () -> reservationService.saveMemberReservation(1L, RESERVATION_WITH_PAYMENT_REQUEST_1))
                .isInstanceOf(DuplicateSaveException.class);
    }

    @DisplayName("이미 대기하고 있는 날짜, 시각, 테마에 같은 멤버가 예약 대기를 추가하는 경우 예외가 발생한다")
    @Test
    void should_throw_exception_when_waiting_date_and_time_and_theme_duplicated_by_same_member() {
        when(reservationRepository.existsByDateValueAndTimeIdAndThemeIdAndMemberId(
                TOMORROW, 1L, 1L, 1L))
                .thenReturn(true);

        assertThatThrownBy(
                () -> reservationService.saveMemberWaitingReservation(1L, RESERVATION_REQUEST_1))
                .isInstanceOf(DuplicateSaveException.class);
    }
}
