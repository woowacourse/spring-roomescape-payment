package roomescape.service.mapper;

import roomescape.dto.LoginMemberWaitingResponse;
import roomescape.dto.ReservationWaitingResponse;

public class LoginMemberWaitingResponseMapper {
    public static LoginMemberWaitingResponse from(ReservationWaitingResponse waitingResponse) {
        return new LoginMemberWaitingResponse(
                waitingResponse.id(),
                waitingResponse.themeName(),
                waitingResponse.date(),
                waitingResponse.startAt(),
                waitingResponse.priority()
        );
    }
}
