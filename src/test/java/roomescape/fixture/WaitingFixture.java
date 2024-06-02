package roomescape.fixture;

import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.theme.Theme;
import roomescape.service.waiting.dto.WaitingRequest;

public class WaitingFixture {
    public static Reservation create(Member member, ReservationDetail reservationDetail) {
        return new Reservation(member, reservationDetail, ReservationStatus.WAITING, Payment.createEmpty());
    }

    public static WaitingRequest createWaitingRequest(Theme theme, ReservationDetail reservationDetail) {
        return new WaitingRequest(reservationDetail.getDate(),
                reservationDetail.getReservationTime().getId(), theme.getId());
    }
}
