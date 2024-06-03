package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminReservationRequest(
        @NotNull(message = "멤버가 입력되지 않았습니다.")
        long memberId,

        @NotBlank(message = "날짜가 입력되지 않았습니다.")
        @Pattern(regexp = "^(?:(?:19|20)\\d{2})-(?:0[1-9]|1[0-2])-(?:0[1-9]|[1-2][0-9]|3[0-1])$")
        String date,

        @NotNull(message = "시간이 입력되지 않았습니다.")
        long timeId,

        @NotNull(message = "테마가 입력되지 않았습니다.")
        long themeId
) {
        public ReservationRequest toReservationRequest() {
                return new ReservationRequest(date, timeId, themeId);
        }
}
