package roomescape.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "예약 생성 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record CreateReservationWithMemberIdWebRequest(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,
        @Schema(description = "예약 날짜", example = "2023-10-01")
        LocalDate date,
        @Schema(description = "예약 시간 ID", example = "1")
        Long timeId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId
) {

    public CreateReservationWithMemberIdWebRequest {
        validate(memberId, date, timeId, themeId);
    }

    private void validate(final Long memberId, final LocalDate date, final Long timeId, final Long themeId) {
        Validator.of(CreateReservationWithMemberIdWebRequest.class)
                .notNullField(Fields.memberId, memberId)
                .notNullField(Fields.date, date)
                .notNullField(Fields.timeId, timeId)
                .notNullField(Fields.themeId, themeId);
    }
}
