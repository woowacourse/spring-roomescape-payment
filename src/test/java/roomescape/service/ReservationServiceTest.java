package roomescape.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.theme.Theme;
import roomescape.exception.reservation.DuplicatedReservationException;
import roomescape.exception.reservation.InvalidDateTimeReservationException;
import roomescape.exception.reservation.NotFoundReservationException;
import roomescape.exception.reservation.ReservationAuthorityNotExistException;
import roomescape.service.payment.PaymentStatus;
import roomescape.service.payment.dto.PaymentCancelOutput;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.dto.ReservationListResponse;
import roomescape.service.reservation.dto.ReservationMineListResponse;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.ReservationSaveInput;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class ReservationServiceTest extends ServiceTest {
    @Autowired
    private ReservationService reservationService;

    @Nested
    @DisplayName("예약 목록 조회")
    class FindAllReservation {
        Theme firstTheme;
        Member user;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            firstTheme = themeFixture.createFirstTheme();
            user = memberFixture.createUserMember();
            Theme secondTheme = themeFixture.createSecondTheme();
            Member admin = memberFixture.createAdminMember();
            reservationFixture.createPastReservation(time, firstTheme, user);
            reservationFixture.createFutureReservation(time, firstTheme, admin);
            reservationFixture.createPastReservation(time, secondTheme, user);
            reservationFixture.createFutureReservation(time, secondTheme, admin);
        }

        @Test
        void 필터링_없이_전체_예약_목록을_조회할_수_있다() {
            ReservationListResponse response = reservationService.searchReservation(
                    null, null, null, null);

            assertThat(response.getReservations())
                    .hasSize(4);
        }

        @Test
        void 예약_목록을_예약자별로_필터링해_조회할_수_있다() {
            ReservationListResponse response = reservationService.searchReservation(
                    user.getId(), null, null, null);

            assertThat(response.getReservations())
                    .hasSize(2);
        }

        @Test
        void 예약_목록을_테마별로_필터링해_조회할_수_있다() {
            ReservationListResponse response = reservationService.searchReservation(
                    null, firstTheme.getId(), null, null);

            assertThat(response.getReservations())
                    .hasSize(2);
        }

        @Test
        void 예약_목록을_기간별로_필터링해_조회할_수_있다() {
            LocalDate dateFrom = LocalDate.of(2000, 4, 1);
            LocalDate dateTo = LocalDate.of(2000, 4, 7);
            ReservationListResponse response = reservationService.searchReservation(
                    null, null, dateFrom, dateTo);

            assertThat(response.getReservations())
                    .hasSize(2);
        }
    }

    @Nested
    @DisplayName("내 예약 목록 조회")
    class FindMyReservation {
        Member member;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            Reservation reservation = reservationFixture.createFutureReservation(time, theme, member);
            paymentFixture.createPayment(reservation);
            waitingFixture.createWaiting(reservation, member);
        }

        @Test
        void 내_예약_목록을_조회할_수_있다() {
            ReservationMineListResponse response = reservationService.findMyReservation(member);

            assertThat(response.getReservations())
                    .hasSize(2);
        }

        @Test
        void 내_예약_목록_조회_시_대기_상태로_몇_번째_대기인지도_확인할_수_있다() {
            ReservationMineListResponse response = reservationService.findMyReservation(member);

            assertThat(response.getReservations().get(1).getStatus())
                    .isEqualTo("1번째 예약대기");
        }
    }

    @Nested
    @DisplayName("예약 추가")
    class SaveReservation {
        ReservationTime time;
        Theme theme;
        Member member;

        @BeforeEach
        void setUp() {
            time = timeFixture.createFutureTime();
            theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
        }

        @Test
        void 예약을_추가할_수_있다() {
            ReservationSaveInput request = new ReservationSaveInput(
                    LocalDate.of(2000, 4, 7), time.getId(), theme.getId());
            ReservationResponse response = reservationService.saveReservationWithoutPayment(request, member);

            assertThat(response.getMember().getName())
                    .isEqualTo(member.getName().getName());
        }

        @Test
        void 시간대와_테마가_똑같은_중복된_예약_추가시_예외가_발생한다() {
            Reservation reservation = reservationFixture.createFutureReservation(time, theme, member);
            ReservationSaveInput input = new ReservationSaveInput(reservation.getDate(), time.getId(), theme.getId());

            assertThatThrownBy(() -> reservationService.saveReservationWithoutPayment(input, member))
                    .isInstanceOf(DuplicatedReservationException.class);
        }

        @Test
        void 지나간_날짜와_시간에_대한_예약_추가시_예외가_발생한다() {
            ReservationSaveInput input = new ReservationSaveInput(
                    LocalDate.of(2000, 4, 6), time.getId(), theme.getId());

            assertThatThrownBy(() -> reservationService.saveReservationWithoutPayment(input, member))
                    .isInstanceOf(InvalidDateTimeReservationException.class);
        }
    }

    @Nested
    @DisplayName("예약 결제")
    class PayReservation {
        Member member;
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            reservation = reservationFixture.createPaymentWaitingReservation(time, theme, member);
        }

        @Test
        void 결제_대기중인_예약을_결제하면_예약상태로_변경되고_결제정보가_추가된다() {
            given(paymentClient.confirmPayment(any())).willReturn(
                    new PaymentConfirmOutput("paymentKey", "orderId", "orderName",
                            1000, ZonedDateTime.now(), ZonedDateTime.now(), PaymentStatus.DONE));

            PaymentConfirmInput paymentConfirmInput = new PaymentConfirmInput("orderId", 1000, "paymentKey");
            reservationService.payReservation(reservation.getId(), paymentConfirmInput, reservation.getMember());

            Reservation payedReservation = reservationFixture.findAllReservation().stream()
                    .filter(reservation -> reservation.equals(this.reservation))
                    .findAny()
                    .get();
            Payment payment = paymentFixture.findByReservation(payedReservation).get();

            assertThat(payedReservation.isBookedStatus()).isTrue();
            assertThat(payment.getReservation()).isEqualTo(payedReservation);
            assertThat(payment.isDoneStatus()).isTrue();
        }
    }

    @Nested
    @DisplayName("예약 취소")
    class CancelReservation {
        Member member;
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            reservation = reservationFixture.createFutureReservation(time, theme, member);
            paymentFixture.createPayment(reservation);
        }

        @Test
        void 예약_id와_예약자_id로_예약을_취소할_수_있다() {
            given(paymentClient.cancelPayment(any()))
                    .willReturn(new PaymentCancelOutput(
                            "paymentKey", "orderId", "orderName", PaymentStatus.CANCELED, ZonedDateTime.now(), ZonedDateTime.now()));

            reservationService.cancelReservation(reservation.getId(), member);
            Payment payment = paymentFixture.findByReservation(reservation).get();
            Reservation canceldReservation = reservationFixture.findAllReservation().stream()
                    .filter(r -> r.equals(reservation))
                    .findAny()
                    .get();

            assertThat(canceldReservation.isCancelStatus()).isTrue();
            assertThat(payment.isCancelStatus()).isTrue();
        }

        @Test
        void 존재하지_않는_예약_id로_예약_취소_시_예외가_발생한다() {
            long wrongReservationId = 10L;

            assertThatThrownBy(() -> reservationService.cancelReservation(wrongReservationId, member))
                    .isInstanceOf(NotFoundReservationException.class);
        }

        @Test
        void 예약자가_아닌_사용자_id로_예약_취소_시_예외가_발생한다() {
            Member anotherMember = memberFixture.createUserMember("another@gmail.com");

            assertThatThrownBy(() -> reservationService.cancelReservation(reservation.getId(), anotherMember))
                    .isInstanceOf(ReservationAuthorityNotExistException.class);
        }

        @Test
        void 예약_대기가_존재하는_예약_취소_시_예약은_삭제되지_않고_대기번호_1번의_대기자가_결제_대기_상태의_예약자로_승격되고_예약_대기가_삭제된다() {
            given(paymentClient.cancelPayment(any()))
                    .willReturn(new PaymentCancelOutput(
                            "paymentKey", "orderId", "orderName", PaymentStatus.CANCELED, ZonedDateTime.now(), ZonedDateTime.now()));

            Member anotherMember = memberFixture.createUserMember("another@gmail.com");
            waitingFixture.createWaiting(reservation, anotherMember);
            reservationService.cancelReservation(reservation.getId(), member);

            Optional<Reservation> paymentWaitingReservation = reservationFixture.findAllReservation().stream()
                    .filter(Reservation::isPaymentWaitingStatus)
                    .findAny();
            Optional<Reservation> canceledReservation = reservationFixture.findAllReservation().stream()
                    .filter(Reservation::isCancelStatus)
                    .findAny();

            assertThat(canceledReservation).isNotEmpty();
            assertThat(canceledReservation.get().getMember()).isEqualTo(reservation.getMember());

            assertThat(paymentWaitingReservation).isNotEmpty();
            assertThat(paymentWaitingReservation.get().getMember()).isEqualTo(anotherMember);

            List<ReservationWaiting> waitings = waitingFixture.findAllWaiting();
            assertThat(waitings)
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("예약 삭제")
    class DeleteReservation {
        Member member;
        Reservation paymentWaitingReservation;
        Reservation canceledReservation;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            paymentWaitingReservation = reservationFixture.createPaymentWaitingReservation(time, theme, member);
            canceledReservation = reservationFixture.createCanceledReservation(time, theme, member);
            paymentFixture.createPayment(canceledReservation);
        }

        @Test
        void 예약_id와_예약자_id로_취소_또는_결제_대기_상태의_예약을_삭제할_수_있다() {
            paymentClient.cancelPayment(any());

            reservationService.deleteReservation(paymentWaitingReservation.getId(), member);
            reservationService.deleteReservation(canceledReservation.getId(), member);

            List<Reservation> deletedReservations = reservationFixture.findAllReservation().stream()
                    .filter(r -> r.equals(paymentWaitingReservation))
                    .filter(r -> r.equals(canceledReservation))
                    .toList();

            assertThat(deletedReservations).isEmpty();
        }

        @Test
        void 예약자가_아닌_사용자_id로_예약_삭제_시_예외가_발생한다() {
            Member anotherMember = memberFixture.createUserMember("another@gmail.com");

            assertThatThrownBy(() -> reservationService.deleteReservation(paymentWaitingReservation.getId(), anotherMember))
                    .isInstanceOf(ReservationAuthorityNotExistException.class);
        }
    }
}
