package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;

@Schema(description = "회원 예약 응답 객체")
public record ReservationForMemberResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "John Doe")
        String memberName,

        @Schema(description = "예약 날짜", example = "2023-12-01")
        LocalDate date,

        @Schema(description = "예약 시간 응답 객체")
        ReservationTimeResponse time,

        @Schema(description = "테마 이름", example = "Escape the Dungeon")
        String themeName,

        @Schema(description = "결제 정보 응답 객체")
        PaymentResponse payment

) {
    public static ReservationForMemberResponse of(Reservation reservation, Payment payment) {
        ReservationTimeResponse timeDto = ReservationTimeResponse.from(reservation.getReservationTime());
        PaymentResponse paymentDto = PaymentResponse.from(payment);
        return new ReservationForMemberResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getDate(),
                timeDto,
                reservation.getTheme().getName(),
                paymentDto
        );
    }
}
