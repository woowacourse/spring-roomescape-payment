package roomescape.reservation.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.reservation.domain.Reservation;

@Slf4j
@Component
public class ReservationProbe {

    public void create(Reservation reservation) {
        log.atInfo().log("create reservation: id={}, date={}, theme={}, time={}, reservationMemberId={}",
                reservation.getId(), reservation.getDate().getValue(), reservation.getTheme().getName().getValue(),
                reservation.getTime().getStartAt(), reservation.getMember().getId());
    }

    public void delete(Reservation reservation) {
        log.atInfo().log("delete reservation: id={}, date={}, theme={}, time={}, reservationMemberId={}",
                reservation.getId(), reservation.getDate().getValue(), reservation.getTheme().getName().getValue(),
                reservation.getTime().getStartAt(), reservation.getMember().getId());
    }
}
