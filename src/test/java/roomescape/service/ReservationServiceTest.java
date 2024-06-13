package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.domain.member.Member;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.theme.Theme;
import roomescape.exception.payment.PaymentConfirmErrorCode;
import roomescape.exception.payment.PaymentConfirmException;
import roomescape.exception.reservation.DuplicatedReservationException;
import roomescape.exception.reservation.InvalidDateTimeReservationException;
import roomescape.exception.reservation.InvalidReservationMemberException;
import roomescape.exception.reservation.NotFoundReservationException;
import roomescape.service.payment.PaymentClient;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.dto.ReservationListResponse;
import roomescape.service.reservation.dto.ReservationMineListResponse;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.ReservationSaveInput;

class ReservationServiceTest extends ServiceTest {
    @Autowired
    private ReservationService reservationService;

    @MockBean
    PaymentClient paymentClient;

    @Nested
    @DisplayName("예약 목록 조회")
    class FindAllReservation {
        Theme firstTheme;
        Member user;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            firstTheme = themeFixture.createFirstTheme();
            user = memberFixture.createUserMember();
            Theme secondTheme = themeFixture.createSecondTheme();
            Member admin = memberFixture.createAdminMember();
            reservationFixture.createPastReservation(reservationTime, firstTheme, user);
            reservationFixture.createFutureReservation(reservationTime, firstTheme, admin);
            reservationFixture.createPastReservation(reservationTime, secondTheme, user);
            reservationFixture.createFutureReservation(reservationTime, secondTheme, admin);
        }

        @Test
        void 필터링_없이_전체_예약_목록을_조회할_수_있다() {
            ReservationListResponse response = reservationService.findAllReservation(
                    null, null, null, null);

            assertThat(response.getReservations().size())
                    .isEqualTo(4);
        }

        @Test
        void 예약_목록을_예약자별로_필터링해_조회할_수_있다() {
            ReservationListResponse response = reservationService.findAllReservation(
                    user.getId(), null, null, null);

            assertThat(response.getReservations().size())
                    .isEqualTo(2);
        }

        @Test
        void 예약_목록을_테마별로_필터링해_조회할_수_있다() {
            ReservationListResponse response = reservationService.findAllReservation(
                    null, firstTheme.getId(), null, null);

            assertThat(response.getReservations().size())
                    .isEqualTo(2);
        }

        @Test
        void 예약_목록을_기간별로_필터링해_조회할_수_있다() {
            LocalDate dateFrom = LocalDate.of(2000, 4, 1);
            LocalDate dateTo = LocalDate.of(2000, 4, 7);
            ReservationListResponse response = reservationService.findAllReservation(
                    null, null, dateFrom, dateTo);

            assertThat(response.getReservations().size())
                    .isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("내 예약 목록 조회")
    class FindMyReservation {
        Member member;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            Member admin = memberFixture.createAdminMember();
            Reservation reservation = reservationFixture.createFutureReservation(reservationTime, theme, admin);
            reservationFixture.createReservationWithDate(LocalDate.of(2000, 4, 9), reservationTime, theme, member);
            waitingFixture.createWaiting(reservation, member);
        }

        @Test
        void 내_예약_목록을_조회할_수_있다() {
            ReservationMineListResponse response = reservationService.findMyReservation(member);

            assertThat(response.getReservations().size())
                    .isEqualTo(2);
        }

        @Test
        void 내_예약_목록_조회_시_대기_상태로_몇_번째_대기인지도_확인할_수_있다() {
            ReservationMineListResponse response = reservationService.findMyReservation(member);

            assertThat(response.getReservations().get(0).getStatus())
                    .isEqualTo(String.format(ReservationStatus.WAITING.getDescription(), 1));
        }
    }

    @Nested
    @DisplayName("결제 없이 예약 추가")
    class SaveReservationWithoutPayment {
        ReservationTime reservationTime;
        Theme theme;
        Member member;

        @BeforeEach
        void setUp() {
            reservationTime = reservationTimeFixture.createFutureReservationTime();
            theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
        }

        @Test
        void 결제_없이_예약을_추가할_수_있다() {
            ReservationSaveInput request = new ReservationSaveInput(
                    LocalDate.of(2000, 4, 7), reservationTime.getId(), theme.getId());
            ReservationResponse response = reservationService.saveReservationWithoutPayment(request, member);

            assertThat(response.getMember().getName())
                    .isEqualTo(member.getName().getName());
        }

        @Test
        void 시간대와_테마가_똑같은_중복된_예약_추가시_예외가_발생한다() {
            Reservation reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
            ReservationSaveInput input = new ReservationSaveInput(reservation.getDate(), reservationTime.getId(),
                    theme.getId());

            assertThatThrownBy(() -> reservationService.saveReservationWithoutPayment(input, member))
                    .isInstanceOf(DuplicatedReservationException.class);
        }

        @Test
        void 지나간_날짜와_시간에_대한_예약_추가시_예외가_발생한다() {
            ReservationSaveInput input = new ReservationSaveInput(
                    LocalDate.of(2000, 4, 6), reservationTime.getId(), theme.getId());

            assertThatThrownBy(() -> reservationService.saveReservationWithoutPayment(input, member))
                    .isInstanceOf(InvalidDateTimeReservationException.class);
        }
    }

    @Nested
    @DisplayName("결제와 함께 예약 추가")
    class SaveReservationWithPayment {
        ReservationTime reservationTime;
        Theme theme;
        Member member;
        ReservationSaveInput reservationSaveInput;
        PaymentConfirmInput paymentConfirmInput;

        @BeforeEach
        void setUp() {
            reservationTime = reservationTimeFixture.createFutureReservationTime();
            theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            reservationSaveInput = new ReservationSaveInput(
                    LocalDate.of(2000, 4, 7), reservationTime.getId(), theme.getId());
            paymentConfirmInput = new PaymentConfirmInput(
                    "orderId", 1000L, "paymentKey");
        }

        @Test
        void 결제와_함께_예약을_추가할_수_있다() {
            PaymentConfirmOutput paymentConfirmOutput = new PaymentConfirmOutput(
                    "paymentKey", "NORMAL", "orderId", "orderName",
                    "KRW", "간편결제", 1000L, PaymentStatus.DONE);
            given(paymentClient.confirmPayment(any()))
                    .willReturn(paymentConfirmOutput);

            reservationService.saveReservationWithPayment(reservationSaveInput, paymentConfirmInput, member);

            List<ReservationPayment> reservationPayments = reservationPaymentFixture.findAllReservationPayment();
            assertThat(reservationPayments.get(0).getInfo().getPaymentKey())
                    .isEqualTo(paymentConfirmOutput.paymentKey());
        }

        @Test
        void 예약_중_결제_실패시_예외가_발생하고_결제와_예약이_모두_추가되지_않는다() {
            given(paymentClient.confirmPayment(any()))
                    .willThrow(new PaymentConfirmException(PaymentConfirmErrorCode.UNKNOWN_PAYMENT_ERROR));

            assertThatThrownBy(() -> reservationService.saveReservationWithPayment(
                    reservationSaveInput, paymentConfirmInput, member))
                    .isInstanceOf(PaymentConfirmException.class);
            assertThat(reservationPaymentFixture.findAllReservationPayment())
                    .isEmpty();
            assertThat(reservationFixture.findAllReservation())
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("예약 삭제")
    class DeleteReservation {
        Member member;
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
        }

        @Test
        void 예약_id와_예약자_id로_예약_대기가_존재하지_않는_예약을_삭제할_수_있다() {
            reservationService.deleteReservation(reservation.getId(), member.getId());

            List<Reservation> reservations = reservationFixture.findAllReservation();
            assertThat(reservations)
                    .isEmpty();
        }

        @Test
        void 존재하지_않는_예약_id로_예약_삭제_시_예외가_발생한다() {
            long wrongReservationId = 10L;

            assertThatThrownBy(() -> reservationService.deleteReservation(wrongReservationId, member.getId()))
                    .isInstanceOf(NotFoundReservationException.class);
        }

        @Test
        void 예약자가_아닌_사용자_id로_예약_삭제_시_예외가_발생한다() {
            long wrongMemberId = 10L;

            assertThatThrownBy(() -> reservationService.deleteReservation(reservation.getId(), wrongMemberId))
                    .isInstanceOf(InvalidReservationMemberException.class);
        }

        @Test
        void 예약_대기가_존재하는_예약_삭제_시_예약은_삭제되지_않고_대기번호_1번의_대기자가_예약자로_승격되면서_예약_대기가_삭제된다() {
            Member otherMember = memberFixture.createAdminMember();
            waitingFixture.createWaiting(reservation, otherMember);

            reservationService.deleteReservation(reservation.getId(), member.getId());

            List<Reservation> reservations = reservationFixture.findAllReservation();
            List<ReservationWaiting> waitings = waitingFixture.findAllWaiting();
            assertThat(reservations)
                    .isNotEmpty()
                    .first()
                    .satisfies(reservation -> {
                        assertThat(reservation).isEqualTo(this.reservation);
                        assertThat(reservation.getMember()).isEqualTo(otherMember);
                    });
            assertThat(waitings)
                    .isEmpty();
        }
    }
}
