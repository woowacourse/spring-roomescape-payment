package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.ReservationStatus;

@Schema(name = "회원의 예약 및 대기 응답", description = "회원의 예약 및 대기 정보 응답시 사용됩니다.")
public record MyReservationResponse(
        @Schema(description = "예약 번호. 예약을 식별할 때 사용합니다.")
        Long id,
        @Schema(description = "테마 이름")
        String themeName,
        @Schema(description = "예약 날짜", type = "string", example = "2022-12-31")
        LocalDate date,
        @Schema(description = "예약 시간", type = "string", example = "09:00")
        LocalTime time,
        @Schema(description = "예약 상태", type = "string")
        ReservationStatus status,
        @Schema(description = "예약 대기 상태일 때의 대기 순번. 확정된 예약은 0의 값을 가집니다.")
        Long rank,
        @Schema(description = "결제 키. 결제가 완료된 예약에만 값이 존재합니다.")
        String paymentKey,
        @Schema(description = "결제 금액. 결제가 완료된 예약에만 값이 존재합니다.")
        Long amount
) {

    public MyReservationResponse(Long id, String themeName, LocalDate date, LocalTime time, ReservationStatus status,
                                 Integer rank, String paymentKey, Long amount) {
        this(id, themeName, date, time, status, rank.longValue(), paymentKey, amount);
    }
}
