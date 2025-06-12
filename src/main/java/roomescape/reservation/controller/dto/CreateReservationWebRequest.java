package roomescape.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "예약 생성 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record CreateReservationWebRequest(
        @Schema(description = "예약 날짜", example = "2023-10-01")
        LocalDate date,
        @Schema(description = "예약 시간 ID", example = "1")
        Long timeId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId,
        @Schema(description = "결제 키", example = "pay_1234567890")
        String paymentKey,
        @Schema(description = "주문 ID", example = "order_1234567890")
        String orderId,
        @Schema(description = "결제 금액", example = "15000")
        Long amount
) {

    public CreateReservationWebRequest {
        validate(date, timeId, themeId, paymentKey, orderId, amount);
    }

    private void validate(
            final LocalDate date,
            final Long timeId,
            final Long themeId,
            final String paymentKey,
            final String orderId,
            final Long amount
    ) {
        Validator.of(CreateReservationWebRequest.class)
                .notNullField(Fields.date, date)
                .notNullField(Fields.timeId, timeId)
                .notNullField(Fields.themeId, themeId)
                .notNullField(Fields.paymentKey, paymentKey)
                .notNullField(Fields.orderId, orderId)
                .notNullField(Fields.amount, amount);
    }
}
