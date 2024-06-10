package roomescape.registration.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.dto.PaymentResponse;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "예약 응답")
public record ReservationResponse(

        @Schema(description = "예약 ID", example = "1")
        long id,

        @Schema(description = "멤버 이름", example = "홍길동")
        String memberName,

        @Schema(description = "테마 이름", example = "홍길동의 모험")
        String themeName,

        @Schema(description = "예약 일자", example = "2099-12-31")
        LocalDate date,

        @Schema(description = "시작 시간", example = "14:00")
        LocalTime startAt,

        PaymentResponse paymentResponse) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getMember().getName(),
                reservation.getTheme().getName(), reservation.getDate(),
                reservation.getReservationTime().getStartAt(), PaymentResponse.getPaymentResponseForNotPaidReservation());
    }
}
