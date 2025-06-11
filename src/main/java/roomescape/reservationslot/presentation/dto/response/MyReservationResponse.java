package roomescape.reservationslot.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.ReservationStatus;

public record MyReservationResponse(@Schema(description = "예약이 만들어진 예약슬롯의 id") Long reservationSlotId,
                                    @Schema(description = "예약한 테마 이름") String theme,
                                    @Schema(description = "예약한 날짜") String date,
                                    @Schema(description = "예약한 시간") String time,
                                    @Schema(description = "예약의 현재 상태") ReservationStatus status,
                                    @Schema(description = "결제가 완료됐을 경우에 사용된 결제 키") String paymentKey,
                                    @Schema(description = "예약을 위해 결제한 금액") Long amount,
                                    @Schema(description = "대기 상태일 시, 대기 순서") long waitingRank) {

}
