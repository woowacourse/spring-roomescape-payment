package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.controller.exception.AuthorizationException;
import roomescape.controller.member.dto.LoginMember;
import roomescape.controller.reservation.dto.CreateReservationRequest;
import roomescape.controller.reservation.dto.ReservationSearchCondition;
import roomescape.domain.Reservation;
import roomescape.domain.Role;
import roomescape.repository.dto.ReservationRankResponse;
import roomescape.repository.dto.WaitingReservationResponse;
import roomescape.service.exception.DuplicateReservationException;
import roomescape.service.exception.InvalidSearchDateException;
import roomescape.service.exception.UserDeleteReservationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Transactional
class ReservationServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReservationService reservationService;

    @Test
    @DisplayName("from, to 날짜가 역순이면 예외가 발생한다.")
    void searchReservationsByReversedFromToThrowsException() {
        LocalDate now = LocalDate.now();
        ReservationSearchCondition condition = new ReservationSearchCondition(1L, 1L, now, now.minusDays(1));
        assertThatThrownBy(() -> reservationService.searchReservations(condition))
                .isInstanceOf(InvalidSearchDateException.class);
    }

    @Test
    @DisplayName("예약을 삭제한다.")
    void deleteReservation() {
        assertThatCode(() -> reservationService.deleteReservation(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("예약을 저장한다.")
    void saveReservation() {
        doNothing().when(paymentService).savePayment(any(CreateReservationRequest.class), any(Reservation.class));

        final long memberId = 3L;
        final long themeId = 4L;
        final long timeId = 1L;
        final LocalDate date = LocalDate.now().plusDays(1);
        final CreateReservationRequest request = new CreateReservationRequest(memberId, themeId, date, timeId, null, null, null);
        final Reservation reservation = reservationService.addUserReservation(request);

        assertAll(
                () -> assertThat(reservation.getMember().getId()).isEqualTo(memberId),
                () -> assertThat(reservation.getTheme().getId()).isEqualTo(themeId),
                () -> assertThat(reservation.getDate()).isEqualTo(date),
                () -> assertThat(reservation.getTime().getId()).isEqualTo(timeId)
        );
    }

    @Test
    @DisplayName("자신의 예약 목록을 조회한다.")
    void getReservationsByMember() {
        final LoginMember member = new LoginMember(3L, "제제", Role.USER);
        final List<ReservationRankResponse> reservationsByMember = reservationService.getMyReservation(member);
        final LocalDate date = LocalDate.now();

        final List<ReservationRankResponse> expected = List.of(
                new ReservationRankResponse(5L, "가을", date.minusDays(7), LocalTime.of(15, 0), 1, "payment_key5",
                        1000),
                new ReservationRankResponse(6L, "가을", date.plusDays(3), LocalTime.of(18, 0), 1, "payment_key6",
                        1000),
                new ReservationRankResponse(8L, "가을", date.plusDays(4), LocalTime.of(18, 0), 2, "payment_key8",
                        1000)
        );

        assertThat(reservationsByMember).isEqualTo(expected);
    }

    @Test
    @DisplayName("예약 대기 목록을 조회한다.")
    void findAllWaiting() {
        final List<WaitingReservationResponse> allWaiting = reservationService.findAllWaiting();
        final List<WaitingReservationResponse> expected = List.of(new WaitingReservationResponse(8L, "제제", "가을",
                LocalDate.now().plusDays(4), LocalTime.of(18, 0)));

        //then
        assertThat(allWaiting).isEqualTo(expected);
    }

    @Test
    @DisplayName("예약 대기를 삭제한다.")
    void deleteWaitReservation() {

        final long waitReservationId = 8L;
        final long memberId = 3L;
        final List<WaitingReservationResponse> beforeDeleting = reservationService.findAllWaiting();
        reservationService.deleteWaitReservation(waitReservationId, memberId);
        final List<WaitingReservationResponse> afterDeleting = reservationService.findAllWaiting();

        assertThat(afterDeleting).hasSize(beforeDeleting.size() - 1);
    }

    @Test
    @DisplayName("다른 회원의 예약 대기를 삭제할 경우 예외가 발생한다.")
    void deleteWaitReservationAnotherUser() {
        final long waitReservationId = 8L;
        final long anotherMemberId = 2L;

        assertThatThrownBy(() -> reservationService.deleteWaitReservation(waitReservationId, anotherMemberId))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("예약 대기가 아닌 예약을 삭제하면 예외가 발생한다.")
    void deleteReservationThrowException() {
        final long reservationId = 1L;
        final long memberId = 1L;
        assertThatThrownBy(() -> reservationService.deleteWaitReservation(reservationId, memberId))
                .isInstanceOf(UserDeleteReservationException.class);
    }

    @Test
    @DisplayName("멤버는 같은 날짜, 테마, 시간에 대해 하나의 예약만 가능하다")
    void duplicateReservationInfo() {
        //given
        doNothing().when(paymentService).savePayment(any(CreateReservationRequest.class), any(Reservation.class));

        final LocalDate date = LocalDate.now().plusDays(10);
        final CreateReservationRequest request = new CreateReservationRequest(3L, 2L, date, 1L,
                "payment_key3", "orderId", 1000L);

        //when && then
        reservationService.addUserReservation(request);
        assertThatThrownBy(() -> reservationService.addUserReservation(request))
                .isInstanceOf(DuplicateReservationException.class);
    }

    @Test
    @DisplayName("다른 멤버가 같은 날짜, 테마, 시간 예약을 할 수 있다.")
    void addReservation() {
        //given
        doNothing().when(paymentService).savePayment(any(CreateReservationRequest.class), any(Reservation.class));

        final LocalDate date = LocalDate.now().plusDays(10);
        final CreateReservationRequest request1 = new CreateReservationRequest(3L, 2L, date, 1L, "paymentKey", null,
                null);
        final CreateReservationRequest request2 = new CreateReservationRequest(2L, 2L, date, 1L, "paymentKey", null,
                null);

        //when && then
        reservationService.addUserReservation(request1);
        assertThatCode(() -> reservationService.addUserReservation(request2))
                .doesNotThrowAnyException();
    }
}
