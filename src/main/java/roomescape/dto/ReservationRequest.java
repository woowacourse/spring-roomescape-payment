package roomescape.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.ReservationStatus;
import roomescape.entity.Member;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;

@Schema(description = "예약 요청 DTO 입니다.")
public record ReservationRequest(
        @Schema(description = "예약 날짜입니다.")
        LocalDate date,
        @Schema(description = "예약 시간 ID 입니다.")
        long timeId,
        @Schema(description = "예약 테마 ID 입니다.")
        long themeId
) {
    public Reservation toReservation(Member member, ReservationTime reservationTime, Theme theme, ReservationStatus reservationStatus) {
        return new Reservation(this.date, reservationTime, theme, member, reservationStatus);
    }

    public static ReservationRequest from(AdminReservationRequest adminReservationRequest) {
        return new ReservationRequest(adminReservationRequest.date(), adminReservationRequest.timeId(), adminReservationRequest.themeId());
    }
    public static ReservationRequest from(ReservationPaymentRequest reservationPaymentRequest){
        return new ReservationRequest(reservationPaymentRequest.date(), reservationPaymentRequest.timeId(), reservationPaymentRequest.themeId());
    }
}
