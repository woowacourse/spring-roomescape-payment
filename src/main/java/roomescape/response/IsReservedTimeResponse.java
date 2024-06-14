package roomescape.response;

import java.time.LocalTime;

public record IsReservedTimeResponse(Long timeId, LocalTime startAt, Boolean alreadyBooked) {

}
