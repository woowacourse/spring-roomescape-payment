package roomescape.domain.reservation;

import java.util.List;
import roomescape.domain.member.Member;

public interface ReservationPaymentRepository {

    void save(ReservationPayment reservationPayment);

    List<ReservationPayment> findAllByMember(Member member);
}
