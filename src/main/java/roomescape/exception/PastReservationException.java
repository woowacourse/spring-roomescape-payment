package roomescape.exception;

import java.time.LocalDateTime;
import roomescape.exception.common.BadRequestException;

public class PastReservationException extends BadRequestException {

    public PastReservationException(LocalDateTime reservationDateTime, LocalDateTime currentDateTime) {
        super("현 시점 이후의 날짜와 시간을 선택해주세요.",
                String.format("[현 시점보다 과거의 예약입니다] 예약 시간 : %s, 현재 시간 : %s",
                        reservationDateTime.toString(), currentDateTime.toString()));
    }
}
