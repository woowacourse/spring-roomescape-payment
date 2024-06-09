package roomescape.service.reservation.module;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.USER_ID;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.ServiceBaseTest;

class ReservationValidatorTest extends ServiceBaseTest {

    @Autowired
    ReservationValidator reservationValidator;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 날짜와_시간대와_테마가_모두_동일한_예약을_등록할_경우_예외_발생() {
        // given
        Reservation reservation = reservationRepository.findByIdOrThrow(1L);

        // when, then
        assertThatThrownBy(() -> reservationValidator.validateReservationAvailability(reservation))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 지나간_날짜로_예약을_등록할_경우_예외_발생() {
        // given
        Reservation reservation = new Reservation(
                LocalDate.now().plusDays(1),
                timeRepository.findByIdOrThrow(1L),
                themeRepository.findByIdOrThrow(10L),
                memberRepository.findByIdOrThrow(USER_ID),
                Status.RESERVED
        );

        // when, then
        assertThatThrownBy(() -> reservationValidator.validateReservationAvailability(reservation))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 확정된_예약으로_대기_예약을_등록할_경우_예외_발생() {
        // given
        Reservation reservation = reservationRepository.findByIdOrThrow(1L);

        // when, then
        assertThatThrownBy(() -> reservationValidator.validateWaitingAddable(reservation))
                .isInstanceOf(RoomEscapeException.class);

    }

    @Test
    void 지나간_날짜로_대기_예약을_등록할_경우_예외_발생() {
        // given
        Reservation reservation = new Reservation(
                LocalDate.now().plusDays(1),
                timeRepository.findByIdOrThrow(1L),
                themeRepository.findByIdOrThrow(10L),
                memberRepository.findByIdOrThrow(USER_ID),
                Status.WAITING
        );

        // when, then
        assertThatThrownBy(() -> reservationValidator.validateWaitingAddable(reservation))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 대기_예약_등록시_사용자에게_이미_동일한_예약이_있을_경우_예외_발생() {
        // given
        Reservation reservation = new Reservation(
                LocalDate.now().plusDays(3),
                timeRepository.findByIdOrThrow(1L),
                themeRepository.findByIdOrThrow(1L),
                memberRepository.findByIdOrThrow(USER_ID),
                Status.WAITING
        );

        // when, then
        assertThatThrownBy(() -> reservationValidator.validateWaitingAddable(reservation))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 이미_결제된_예약일_경우_예외_발생() {
        // given
        Reservation reservation = reservationRepository.findByIdOrThrow(1L);

        // when, then
        assertThatThrownBy(() -> reservationValidator.validatePaymentAvailability(reservation.getId()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 지나간_날짜의_예약을_취소할_경우_예외_발생() {
        // given
        Reservation reservation = reservationRepository.findByIdOrThrow(1L);

        // when, then
        assertThatThrownBy(() -> reservationValidator.validateReservationCancellation(reservation))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 확정된_예약이_있는_상태에서_결제_대기로_승인하려_하는_경우_예외_발생() {
        // given
        Reservation reservation = reservationRepository.findByIdOrThrow(31L);

        // when, then
        assertThatThrownBy(() -> reservationValidator.validateApproval(reservation))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 결제_대기가_아닌_경우_예외_발생() {
        // given
        Reservation reservation = reservationRepository.findByIdOrThrow(31L);

        // when, then
        assertThatThrownBy(() -> reservationValidator.validatePaymentPendingStatus(reservation))
                .isInstanceOf(RoomEscapeException.class);
    }
}
