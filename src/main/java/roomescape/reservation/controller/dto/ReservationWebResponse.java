package roomescape.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.theme.controller.dto.ThemeWebResponse;
import roomescape.time.controller.dto.ReservationTimeWebResponse;

@Schema(description = "예약 정보 응답")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record ReservationWebResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,
        @Schema(description = "회원 정보")
        MemberInfo memberInfo,
        @Schema(description = "예약 날짜", example = "2023-10-01")
        LocalDate date,
        @Schema(description = "예약 시간")
        ReservationTimeWebResponse time,
        @Schema(description = "테마 정보")
        ThemeWebResponse theme
) {

    public ReservationWebResponse {
        validate(id, memberInfo, date, time, theme);
    }

    private void validate(final Long id,
                          final MemberInfo memberInfo,
                          final LocalDate date,
                          final ReservationTimeWebResponse time,
                          final ThemeWebResponse theme) {
        Validator.of(ReservationWebResponse.class)
                .notNullField(Fields.id, id)
                .notNullField(Fields.memberInfo, memberInfo)
                .notNullField(Fields.date, date)
                .notNullField(Fields.time, time)
                .notNullField(Fields.theme, theme);
    }
}
