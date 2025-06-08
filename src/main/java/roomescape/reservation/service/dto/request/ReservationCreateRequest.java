package roomescape.reservation.service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import roomescape.auth.service.dto.LoginMember;

import java.time.LocalDate;

@Schema(name = "ReservationCreateRequest(예약 생성 요청 DTO)")
public record ReservationCreateRequest(
        @FutureOrPresent @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotNull LoginMember loginMember
) {
}
