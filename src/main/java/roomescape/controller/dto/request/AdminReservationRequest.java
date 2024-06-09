package roomescape.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import roomescape.service.dto.request.ReservationCreateRequest;

import java.time.LocalDate;

public record AdminReservationRequest(
        @NotNull(message = "날짜는 필수 값입니다.")
        LocalDate date,

        @NotNull(message = "테마 id는 필수 값입니다.")
        @Positive
        Long themeId,

        @NotNull(message = "시간 id는 필수 값입니다.")
        @Positive
        Long timeId,

        @NotNull(message = "회원 id는 필수 값입니다.")
        @Positive
        Long memberId
) {

    public ReservationCreateRequest toCreateReservationRequest() {
        return new ReservationCreateRequest(date, timeId, themeId, memberId, null, null, null);
    }
}
