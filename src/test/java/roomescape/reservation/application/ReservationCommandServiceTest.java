package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.common.exception.impl.BadRequestException;
import roomescape.common.exception.impl.NotFoundException;
import roomescape.payment.application.dto.PaymentDataRequest;
import roomescape.reservation.ReservationTestConfig;
import roomescape.reservation.application.dto.AdminReservationRequest;
import roomescape.reservation.application.dto.MemberReservationRequest;
import roomescape.reservation.application.dto.MemberWaitingRequest;
import roomescape.reservation.application.dto.ReservationResponse;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@Import(ReservationTestConfig.class)
class ReservationCommandServiceTest {

    @Autowired
    private ReservationCommandService reservationCommandService;

    @Test
    void 예약을_추가한다() {
        // given
        final LocalDate reservationDate = LocalDate.now().plusDays(1);
        final Long timeId = 1L;
        final Long themeId = 1L;
        final Long memberId = 1L;

        final MemberReservationRequest request = new MemberReservationRequest(
                reservationDate,
                timeId,
                themeId,
                "dummy",
                "dummy",
                BigDecimal.valueOf(1000),
                "NORMAL"
        );

        final PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy",
                "dummy",
                BigDecimal.valueOf(1000)
        );

        // when
        ReservationResponse response = reservationCommandService.addMemberReservation(
                request,
                memberId,
                paymentDataRequest
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.date()).isEqualTo(reservationDate);
        assertThat(response.time().id()).isEqualTo(timeId);
        assertThat(response.theme().id()).isEqualTo(themeId);
        assertThat(response.member().id()).isEqualTo(memberId);
    }


    @Test
    void 예약을_삭제한다() {
        assertThatCode(() -> reservationCommandService.deleteReservationById(8L))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_예약은_삭제할_수_없다() {
        assertThatThrownBy(() -> reservationCommandService.deleteReservationById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 예약입니다.");
    }

    @Test
    void 대기_예약을_확정_예약으로_변경한다() {
        PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy", "dummy", BigDecimal.valueOf(1000)
        );

        reservationCommandService.deleteReservationById(8L);

        assertThatCode(() -> reservationCommandService.acceptReservation(2L, paymentDataRequest))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_예약은_상태를_변경할_수_없다() {
        PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy", "dummy", BigDecimal.valueOf(1000)
        );

        assertThatThrownBy(() -> reservationCommandService.acceptReservation(999L, paymentDataRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 예약입니다.");
    }

    @Test
    void 과거_시간에는_예약할_수_없다() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy", "dummy", BigDecimal.ZERO
        );
        MemberReservationRequest request = new MemberReservationRequest(
                pastDate, 1L, 1L, "dummy", "dummy", BigDecimal.ZERO, "NORMAL"
        );

        assertThatThrownBy(() -> reservationCommandService.addMemberReservation(request, 1L, paymentDataRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("현재보다 과거의 날짜로 예약할 수 없습니다.");
    }

    @Test
    void 관리자가_예약을_추가한다() {
        AdminReservationRequest request = new AdminReservationRequest(LocalDate.now().plusDays(1), 1L, 1L, 1L);

        ReservationResponse response = reservationCommandService.addAdminReservation(request);
        assertThat(response.date()).isEqualTo(request.date());
        assertThat(response.member().id()).isEqualTo(1L);
    }

    @Test
    void 중복된_예약이나_대기는_불가하다() {
        PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy", "dummy", BigDecimal.valueOf(1000)
        );

        MemberReservationRequest reservation = new MemberReservationRequest(
                LocalDate.now().plusDays(1), 1L, 1L, "dummy", "dummy", BigDecimal.valueOf(1000), "NORMAL"
        );
        reservationCommandService.addMemberReservation(reservation, 1L, paymentDataRequest);

        MemberWaitingRequest waiting = new MemberWaitingRequest(LocalDate.now().plusDays(1), 1L, 1L);
        assertThatThrownBy(() -> reservationCommandService.addMemberWaiting(waiting, 1L))
                .isInstanceOf(roomescape.common.exception.impl.ConflictException.class)
                .hasMessage("이미 예약 확정 및 대기 건수가 있습니다.");
    }

    @Test
    void 본인의_대기가_아니면_삭제할_수_없다() {
        assertThatThrownBy(() -> reservationCommandService.cancelOwnWaitingById(2L, 999L))
                .isInstanceOf(roomescape.common.exception.impl.BadRequestException.class)
                .hasMessage("사용자 본인의 예약이 아닙니다.");
    }

    @Test
    void 이미_예약된_시간은_확정할_수_없다() {
        PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy", "dummy", BigDecimal.valueOf(1000)
        );
        assertThatThrownBy(() -> reservationCommandService.acceptReservation(1L, paymentDataRequest))
                .isInstanceOf(roomescape.common.exception.impl.ConflictException.class)
                .hasMessage("이미 예약 확정된 건이 있습니다.");
    }
}
