package roomescape.fixture;

import java.util.List;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationDetail;

public class ReservationFixture {
    public static List<Reservation> createReservations(List<ReservationDetail> details, Member member, Status status) {
        return details.stream()
                .map(detail -> createReservation(detail, member, status))
                .toList();
    }

    public static Reservation createReservation(ReservationDetail detail, Member member, Status status) {
        return new Reservation(member, detail, status);
    }
}
