package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.service.dto.MyReservationInfo;
import roomescape.reservation.service.dto.ReservationInfo;

public record MyReservationResponse(long id, String themeName, LocalDate date, LocalTime time,
                                    String status) {
    public static MyReservationResponse from(MyReservationInfo myReservationInfo) {
        final ReservationInfo reservationInfo = myReservationInfo.reservationInfo();
        return new MyReservationResponse(
                myReservationInfo.id(),
                reservationInfo.themeName(),
                reservationInfo.date(),
                reservationInfo.time(),
                myReservationInfo.waitingResponse().getStatus()
        );
    }
}
