package roomescape.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "예약 생성 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record ReservationSearchWebRequest(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId,
        @Schema(description = "시작 날짜", example = "2023-10-01")
        LocalDate from,
        @Schema(description = "종료 날짜", example = "2023-10-31")
        LocalDate to
) {

    public ReservationSearchWebRequest {
        validate(memberId, themeId, from, to);
    }

    private void validate(final Long memberId,
                          final Long themeId,
                          final LocalDate from,
                          final LocalDate to) {
        Validator.of(ReservationSearchWebRequest.class)
                .notNullField(Fields.memberId, memberId)
                .notNullField(Fields.themeId, themeId)
                .notNullField(Fields.from, from)
                .notNullField(Fields.to, to);
    }
}
