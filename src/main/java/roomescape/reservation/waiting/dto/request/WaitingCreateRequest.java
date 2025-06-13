package roomescape.reservation.waiting.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.request.ReservationRequest;

public record WaitingCreateRequest(
        @FutureOrPresent @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotNull LoginMember loginMember
) {

    public static WaitingCreateRequest from(final ReservationRequest request, final LoginMember loginMember) {
        return new WaitingCreateRequest(request.date(), request.timeId(), request.themeId(), loginMember);
    }
}
