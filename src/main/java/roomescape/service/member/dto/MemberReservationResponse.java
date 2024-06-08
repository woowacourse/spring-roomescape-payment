package roomescape.service.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.dto.ReservationWithRank;

public record MemberReservationResponse(
    Long reservationId,
    String theme,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate date,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul") LocalTime time,
    ReservationStatusResponse reservationStatus,
    PaymentResponse payment
) {

    public static MemberReservationResponse createUnpaid(ReservationWithRank reservationWithRank) {
        return new MemberReservationResponse(
            reservationWithRank.getReservation().getId(),
            reservationWithRank.getReservation().getTheme().getName().getValue(),
            reservationWithRank.getReservation().getDate(),
            reservationWithRank.getReservation().getTime(),
            new ReservationStatusResponse(reservationWithRank.getReservation().getStatus().getDescription(), reservationWithRank.getRank()),
            new PaymentResponse("없음", 0L)
        );
    }
}
