package roomescape.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record LoginMemberWaitingResponse(long reservationId,
                                         String theme,
                                         LocalDate date,
                                         LocalTime time,
                                         long priority) {
}
