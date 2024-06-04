package roomescape.payment.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.common.ServiceTest;
import roomescape.member.application.MemberService;
import roomescape.member.domain.Member;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.reservation.application.BookingManageService;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.application.ThemeService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.event.ReservationSavedEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;

class PaymentServiceTest extends ServiceTest {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingManageService bookingManageService;

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("발행된 이벤트의 예약과 결제 정보로 결제 내역을 생성한다.")
    void create() {
        // given
        ReservationTime time = reservationTimeService.create(new ReservationTime(MIA_RESERVATION_TIME));
        Theme theme = themeService.create(WOOTECO_THEME());
        Member member = memberService.create(USER_MIA());
        Reservation reservation = bookingManageService.create(MIA_RESERVATION(time, theme, member, BOOKING));

        ConfirmedPayment confirmedPayment = new ConfirmedPayment("paymentKey", "orderId", 10);
        ReservationSavedEvent reservationSavedEvent = new ReservationSavedEvent(reservation, confirmedPayment);

        // when
        paymentService.create(reservationSavedEvent);

        // then
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);
    }
}
