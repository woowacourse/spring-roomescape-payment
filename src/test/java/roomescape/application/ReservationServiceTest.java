package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.domain.reservation.Status.CANCELED;
import static roomescape.domain.reservation.Status.PAYMENT_PENDING;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.domain.reservation.Status.WAITING;
import static roomescape.support.fixture.MemberFixture.MEMBER_JAZZ;
import static roomescape.support.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.support.fixture.ThemeFixture.THEME_BED;
import static roomescape.support.fixture.TimeFixture.ONE_PM;
import static roomescape.support.fixture.TimeFixture.TWO_PM;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.request.reservation.ReservationPaymentRequest;
import roomescape.application.dto.request.reservation.ReservationRequest;
import roomescape.application.dto.request.reservation.UserReservationRequest;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.exception.member.AuthenticationFailureException;
import roomescape.exception.reservation.DuplicatedReservationException;
import roomescape.exception.reservation.NotFoundReservationException;
import roomescape.exception.theme.NotFoundThemeException;
import roomescape.exception.time.NotFoundReservationTimeException;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.infrastructure.repository.ReservationTimeRepository;
import roomescape.infrastructure.repository.ThemeRepository;
import roomescape.support.DatabaseCleanupListener;
import roomescape.support.fake.FakePayment;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class,
})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ReservationRepository reservationRepository;

    Reservation reservation(Member member, Theme theme, String date, ReservationTime time, Status status) {
        return new Reservation(member, theme, LocalDate.parse(date), time, status);
    }

    @DisplayName("예약을 시도하는 회원이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_save_reservation_not_exists_member() {
        memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        UserReservationRequest request = new UserReservationRequest(date, onePm.getId(), bed.getId(),
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        assertThatThrownBy(() -> reservationService.saveReservation(request, 2L))
                .isInstanceOf(AuthenticationFailureException.class);
    }

    @DisplayName("존재하지 않는 테마에 예약을 시도하면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_save_reservation_not_exists_theme() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        UserReservationRequest request = new UserReservationRequest(date, onePm.getId(), 2L,
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        assertThatThrownBy(() -> reservationService.saveReservation(request, jazz.getId()))
                .isInstanceOf(NotFoundThemeException.class);
    }

    @DisplayName("존재하지 않는 시간에 예약을 시도하면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_save_reservation_not_exists_time() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        UserReservationRequest request = new UserReservationRequest(date, 2L, bed.getId(),
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        assertThatThrownBy(() -> reservationService.saveReservation(request, jazz.getId()))
                .isInstanceOf(NotFoundReservationTimeException.class);
    }

    @DisplayName("예약하려는 시간이 현재보다 이전 시간이라면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_reservation_past_time() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().minusDays(1);

        UserReservationRequest request = new UserReservationRequest(date, onePm.getId(), bed.getId(),
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        assertThatThrownBy(() -> reservationService.saveReservation(request, jazz.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 해당 회원의 중복된 예약이 존재하면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_already_duplicated_reservation() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        reservationRepository.save(reservation(jazz, bed, date.toString(), onePm, RESERVED));

        UserReservationRequest request = new UserReservationRequest(date, onePm.getId(), bed.getId(),
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        assertThatThrownBy(() -> reservationService.saveReservation(request, jazz.getId()))
                .isInstanceOf(DuplicatedReservationException.class);
    }

    @DisplayName("결제 대기 상태 혹은 예약 확정된 예약이 이미 존재하면 대기 상태인 예약을 생성한다.")
    @Test
    void get_waiting_reservation_when_exists_already_pending_or_reserved_reservation() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Member sun = memberRepository.save(MEMBER_SUN.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        reservationRepository.save(reservation(jazz, bed, date.toString(), onePm, RESERVED));

        UserReservationRequest request = new UserReservationRequest(date, onePm.getId(), bed.getId(),
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        ReservationResponse response = reservationService.saveReservation(request, sun.getId());

        assertThat(response.status()).isEqualTo(WAITING);
    }

    @DisplayName("결제 대기 상태 혹은 예약 확정된 예약이 존재하지 않으면 예약 상태인 예약을 생성한다.")
    @Test
    void get_reserved_reservation_when_not_exists_pending_or_reserved_reservation() {
        Member sun = memberRepository.save(MEMBER_SUN.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        UserReservationRequest request = new UserReservationRequest(date, onePm.getId(), bed.getId(),
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        ReservationResponse response = reservationService.saveReservation(request, sun.getId());

        assertThat(response.status()).isEqualTo(RESERVED);
    }

    @DisplayName("결제가 성공하고 예약을 정상적으로 생성한다.")
    @Test
    void success_save_reservation() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        UserReservationRequest request = new UserReservationRequest(date, onePm.getId(), bed.getId(),
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        assertThatNoException()
                .isThrownBy(() -> reservationService.saveReservation(request, jazz.getId()));
    }

    @DisplayName("어드민이 예약을 시도하는 회원이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_save_reservation_not_exists_member_by_admin() {
        memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        ReservationRequest request = new ReservationRequest(date, 2L, onePm.getId(), bed.getId());

        assertThatThrownBy(() -> reservationService.saveReservationByAdmin(request))
                .isInstanceOf(AuthenticationFailureException.class);
    }

    @DisplayName("어드민이 예약을 정상적으로 생성한다.")
    @Test
    void success_save_reservation_by_admin() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        ReservationRequest request = new ReservationRequest(date, jazz.getId(), onePm.getId(), bed.getId());

        assertThatNoException()
                .isThrownBy(() -> reservationService.saveReservationByAdmin(request));
    }

    @DisplayName("결제를 시도한 예약이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_payment_for_not_exists_reservation() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        reservationRepository.save(reservation(jazz, bed, date.toString(), onePm, PAYMENT_PENDING));

        ReservationPaymentRequest request = new ReservationPaymentRequest(2L,
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        assertThatThrownBy(() -> reservationService.paymentForPending(request, jazz.getId()))
                .isInstanceOf(NotFoundReservationException.class);
    }

    @DisplayName("결제를 시도한 회원과 예약의 주인이 일치하지 않으면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_payment_mismatch_member() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        reservationRepository.save(reservation(jazz, bed, date.toString(), onePm, PAYMENT_PENDING));

        ReservationPaymentRequest request = new ReservationPaymentRequest(1L,
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        assertThatThrownBy(() -> reservationService.paymentForPending(request, 2L))
                .isInstanceOf(AuthenticationFailureException.class);
    }

    @DisplayName("결제가 정상적으로 완료되고 예약의 상태를 PAYMENT_PENDING 에서 RESERVED 으로 변경한다.")
    @Test
    void success_payment_for_pending() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        reservationRepository.save(reservation(jazz, bed, date.toString(), onePm, PAYMENT_PENDING));

        ReservationPaymentRequest request = new ReservationPaymentRequest(1L,
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        ReservationResponse response = reservationService.paymentForPending(request, 1L);

        assertThat(response.status()).isEqualTo(RESERVED);
    }

    @DisplayName("예약 상태인 예약들을 모두 조회한다.")
    @Test
    void find_all_reservations_with_reserved() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        ReservationTime twoPm = timeRepository.save(TWO_PM.create());

        LocalDate date1 = LocalDate.now().plusDays(1);
        LocalDate date2 = LocalDate.now().plusDays(2);

        Reservation reserved = reservationRepository.save(reservation(jazz, bed, date1.toString(), onePm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, date1.toString(), twoPm, WAITING));
        reservationRepository.save(reservation(jazz, bed, date2.toString(), onePm, PAYMENT_PENDING));
        reservationRepository.save(reservation(jazz, bed, date2.toString(), twoPm, CANCELED));

        List<ReservationResponse> response = reservationService.findAllReservationsWithReserved();

        assertThat(response).usingRecursiveComparison()
                .isEqualTo(List.of(ReservationResponse.from(reserved)));
    }

    @DisplayName("대기 상태인 예약들을 모두 조회한다.")
    @Test
    void find_all_reservations_with_waiting() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        ReservationTime twoPm = timeRepository.save(TWO_PM.create());

        LocalDate date1 = LocalDate.now().plusDays(1);
        LocalDate date2 = LocalDate.now().plusDays(2);

        reservationRepository.save(reservation(jazz, bed, date1.toString(), onePm, RESERVED));
        Reservation waiting = reservationRepository.save(reservation(jazz, bed, date1.toString(), twoPm, WAITING));
        reservationRepository.save(reservation(jazz, bed, date2.toString(), onePm, PAYMENT_PENDING));
        reservationRepository.save(reservation(jazz, bed, date2.toString(), twoPm, CANCELED));

        List<ReservationResponse> response = reservationService.findAllWaitings();

        assertThat(response).usingRecursiveComparison()
                .isEqualTo(List.of(ReservationResponse.from(waiting)));
    }
}
