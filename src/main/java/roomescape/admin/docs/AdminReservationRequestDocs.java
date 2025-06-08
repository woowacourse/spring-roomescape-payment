package roomescape.admin.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.admin.dto.AdminReservationRequest;

import java.time.LocalDate;

@Schema(description = "관리자 예약 생성 요청")
public record AdminReservationRequestDocs(
        @Schema(description = "예약 일자", example = "2099-06-08")
        LocalDate date,
        @Schema(description = "시간 ID", example = "1")
        Long timeId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId,
        @Schema(description = "멤버 ID", example = "1")
        Long memberId) {

    public AdminReservationRequest toReservationRequest() {
        return new AdminReservationRequest(
                this.date,
                this.timeId,
                this.themeId,
                this.memberId
        );
    }
}
