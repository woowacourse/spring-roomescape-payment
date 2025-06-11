package roomescape.integration.fixture;

import org.springframework.stereotype.Component;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.domain.PaymentKey;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.schedule.domain.ReservationSchedule;

@Component
public class ReservationDbFixture {
    private ReservationRepository reservationRepository;

    public ReservationDbFixture(final ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation 예약_생성(
            final ReservationSchedule schedule,
            final Member member
    ) {
        return reservationRepository.save(new Reservation(null, member, schedule));
    }

    public Reservation 예약_생성(
            final ReservationSchedule schedule,
            final Member member,
            final PaymentInfo paymentInfo
    ) {
        return reservationRepository.save(new Reservation(
                null,
                member,
                schedule,
                paymentInfo.orderId(),
                paymentInfo.totalAmount(),
                paymentInfo.paymentKey()
        ));
    }
}
