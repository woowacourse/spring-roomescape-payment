package roomescape.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "예약 정보 응답")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record ReservationWithStatusResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,
        @Schema(description = "테마 이름", example = "시소의 방탈출")
        String themeName,
        @Schema(description = "예약 날짜", example = "2023-10-01")
        LocalDate date,
        @Schema(description = "예약 시간", example = "14:00")
        LocalTime time,
        @Schema(description = "예약 상태", example = "예약완료")
        String status,
        @Schema(description = "결제 키", example = "pay_1234567890")
        String paymentKey,
        @Schema(description = "결제 금액", example = "15000")
        Long amount
) {

    public ReservationWithStatusResponse {
        validate(id, themeName, date, time, status);
    }

    public ReservationWithStatusResponse(
            Long id,
            String themeName,
            LocalDate date,
            LocalTime time,
            String status
    ) {
        this(id, themeName, date, time, status, null, null);
    }

    public ReservationWithStatusResponse(
            Long id,
            String themeName,
            LocalDate date,
            LocalTime time,
            Integer rank
    ) {
        this(id, themeName, date, time, rank.toString() + "번째 예약대기", null, null);
    }

    private void validate(
            final Long id,
            final String themeName,
            final LocalDate date,
            final LocalTime time,
            final String status
    ) {
        Validator.of(ReservationWithStatusResponse.class)
                .notNullField(Fields.id, id)
                .notNullField(Fields.themeName, themeName)
                .notNullField(Fields.date, date)
                .notNullField(Fields.time, time)
                .notNullField(Fields.status, status);
    }

}
