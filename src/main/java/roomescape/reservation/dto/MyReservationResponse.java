package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.dto.WaitingReservationRanking;
import roomescape.reservation.domain.entity.MemberReservation;
import roomescape.reservation.domain.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "사용자별 예약 응답 DTO")
public record MyReservationResponse(
        @Schema(description = "예약 pk", example = "1") Long reservationId,
        @Schema(description = "테마 이름", example = "공포") String theme,
        @Schema(description = "예약 날짜", example = "2024-06-07") LocalDate date,
        @Schema(description = "예약 시간") LocalTime time,
        @Schema(description = "예약 상태", example = "예약") String status,
        @Schema(description = "결제 키") String paymentKey,
        @Schema(description = "결제 금액", example = "21000.00") String amount
) {

    public static MyReservationResponse from(MemberReservation memberReservation) {
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                memberReservation.getStatus().getStatusName(),
                null,
                null
        );
    }

    public static MyReservationResponse of(MemberReservation memberReservation,
                                           String plainPaymentKey,
                                           BigDecimal amount
    ) {
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                memberReservation.getStatus().getStatusName(),
                plainPaymentKey,
                amount.toPlainString()
        );
    }

    public static MyReservationResponse from(WaitingReservationRanking waitingReservationRanking) {
        MemberReservation memberReservation = waitingReservationRanking.getMemberReservation();
        String status = waitingReservationRanking.getDisplayRank() + "번째 " + memberReservation.getStatus().getStatusName();
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                status,
                null,
                null
        );
    }
}
