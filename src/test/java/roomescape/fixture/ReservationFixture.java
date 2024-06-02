package roomescape.fixture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.service.reservation.dto.AdminReservationRequest;
import roomescape.service.reservation.dto.ReservationRequest;

import java.time.LocalDate;

public class ReservationFixture {
    public static Reservation createReserved(Member member, ReservationDetail reservationDetail) {
        return new Reservation(member, reservationDetail, ReservationStatus.RESERVED, Payment.createEmpty());
    }

    public static AdminReservationRequest createAdminReservationRequest(Member admin, ReservationDetail reservationDetail) {
        return new AdminReservationRequest(reservationDetail.getDate(), admin.getId(),
                reservationDetail.getReservationTime().getId(), reservationDetail.getTheme().getId());
    }

    public static ReservationRequest createReservationRequest(Schedule schedule, Theme theme) {
        return new ReservationRequest(schedule.getDate(), schedule.getReservationTime().getId(), theme.getId(), "paymentKey", "orderId", 1000L);
    }

    public static ReservationRequest createReservationRequest(Schedule schedule, Long themeId) {
        return new ReservationRequest(schedule.getDate(), schedule.getReservationTime().getId(), themeId, "paymentKey", "orderId", 1000L);
    }

    public static ReservationRequest createReservationRequest(LocalDate date, Long timeId, Theme theme) {
        return new ReservationRequest(date, timeId, theme.getId(), "paymentKey", "orderId", 1000L);
    }
}
