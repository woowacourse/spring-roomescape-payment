package roomescape.reservation.service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import roomescape.auth.service.dto.LoginMember;

import java.time.LocalDate;

public record ReservationCreateRequest(
        @FutureOrPresent @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotNull LoginMember loginMember
) {

    public static ReservationCreateRequest from(final ReservationRequest request, final LoginMember loginMember) {
        return new ReservationCreateRequest(request.date(), request.timeId(), request.themeId(), loginMember);
    }

    public static ReservationCreateRequest from(final ReservationWithPaymentRequest request, final LoginMember loginMember) {
        return new ReservationCreateRequest(request.date(), request.timeId(), request.themeId(), loginMember);
    }
}
