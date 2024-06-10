package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record AdminReservationAddRequest(

        @Schema(description = "예약 날짜", example = "2099-12-31")
        @NotNull(message = "예약 날짜는 필수 입니다.")
        LocalDate date,

        @Schema(description = "예약자 id", example = "1")
        @NotNull(message = "멤버 아이디는 필수 입니다.")
        @Positive
        Long memberId,

        @Schema(description = "예약 시간 id", example = "1")
        @NotNull(message = "예약 시간 선택은 필수 입니다.")
        @Positive
        Long timeId,

        @Schema(description = "예약 테마 id", example = "1")
        @NotNull(message = "테마 선택은 필수 입니다.")
        @Positive
        Long themeId
) {

    public MemberReservationAddRequest toMemberRequest() {
        return new MemberReservationAddRequest(date, timeId, themeId);
    }
}
