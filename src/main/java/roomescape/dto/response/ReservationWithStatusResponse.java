package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.Status;
import roomescape.domain.WaitingWithRank;

@Schema(description = "예약 상태가 포함된 응답 객체")
public record ReservationWithStatusResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "John Doe")
        String memberName,

        @Schema(description = "예약 날짜", example = "2023-12-01")
        LocalDate date,

        @Schema(description = "예약 시간 정보")
        ReservationTimeResponse time,

        @Schema(description = "테마 이름", example = "Escape the Castle")
        String themeName,

        @Schema(description = "예약 상태", example = "CONFIRMED 또는 '5번째 예약대기'")
        String status,

        @Schema(description = "결제 정보")
        PaymentResponse payment
) {
    public static ReservationWithStatusResponse of(Reservation reservation, Payment payment) {
        ReservationTimeResponse timeDto = ReservationTimeResponse.from(reservation.getReservationTime());
        PaymentResponse paymentDto = PaymentResponse.from(payment);
        return new ReservationWithStatusResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getDate(),
                timeDto,
                reservation.getTheme().getName(),
                Status.CONFIRMED.toString(),
                paymentDto
        );
    }

    public static ReservationWithStatusResponse of(WaitingWithRank waitingWithRank) {
        ReservationTimeResponse dto = ReservationTimeResponse.from(waitingWithRank.waiting().getReservationTime());
        return new ReservationWithStatusResponse(
                waitingWithRank.waiting().getId(),
                waitingWithRank.waiting().getMember().getName(),
                waitingWithRank.waiting().getDate(),
                dto,
                waitingWithRank.waiting().getTheme().getName(),
                String.format("%d번째 예약대기", waitingWithRank.rank()),
                null
        );
    }
}
