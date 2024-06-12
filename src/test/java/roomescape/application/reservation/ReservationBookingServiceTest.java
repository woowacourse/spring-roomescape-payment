package roomescape.application.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static roomescape.fixture.MemberFixture.ADMIN_PK;
import static roomescape.fixture.MemberFixture.MEMBER_PK;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import roomescape.application.ServiceTest;
import roomescape.application.reservation.dto.request.ReservationPaymentRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.exception.UnAuthorizedException;
import roomescape.exception.reservation.AlreadyBookedException;
import roomescape.fixture.ReservationFixture;

@ServiceTest
@Import(ReservationFixture.class)
class ReservationBookingServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationFixture reservationFixture;

    @Test
    @DisplayName("중복된 예약을 하는 경우 예외를 반환한다.")
    void shouldReturnIllegalStateExceptionWhenDuplicatedReservationCreate() {
        Reservation reservation = reservationFixture.saveReservation();
        Member member = reservation.getMember();
        ReservationTime reservationTime = reservation.getTime();
        Theme theme = reservation.getTheme();

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                member.getId(),
                theme.getId(), LocalDate.of(2024, 1, 1),
                reservationTime.getId(),
                "paymentKey",
                "orderId"
        );
        assertThatCode(() -> reservationService.createReservation(request))
                .isInstanceOf(AlreadyBookedException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청시 예약이 존재하면 예약을 삭제한다.")
    void shouldDeleteReservationWhenReservationExist() {
        Reservation reservation = reservationFixture.saveReservation();
        Member member = reservation.getMember();
        reservationService.cancelReservation(member.getId(), reservation.getId());

        List<Reservation> reservations = reservationRepository.findAllBookedReservations();
        assertThat(reservations).isEmpty();
    }

    @Test
    @DisplayName("다른 사람의 예약을 삭제하는 경우, 예외를 반환한다.")
    void shouldThrowExceptionWhenDeleteOtherMemberReservation() {
        Long reservationId = reservationFixture.saveReservation().getId();
        long memberId = memberRepository.save(MEMBER_PK.create()).getId();
        assertThatCode(() -> reservationService.cancelReservation(memberId, reservationId))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    @DisplayName("관리자가 다른 사람의 예약을 삭제하는 경우, 예약이 삭제된다.")
    void shouldDeleteReservationWhenAdmin() {
        Reservation reservation = reservationFixture.saveReservation();
        Member admin = memberRepository.save(ADMIN_PK.create());
        reservationService.cancelReservation(admin.getId(), reservation.getId());

        List<Reservation> reservations = reservationRepository.findAllBookedReservations();
        assertThat(reservations).isEmpty();
    }

    @Test
    @DisplayName("예약 삭제 요청시 예약이 존재하지 않으면 예외를 반환한다.")
    void shouldThrowsIllegalArgumentExceptionWhenReservationDoesNotExist() {
        assertThatCode(() -> reservationService.cancelReservation(1L, 99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("존재하지 않는 예약입니다.");
    }
}
