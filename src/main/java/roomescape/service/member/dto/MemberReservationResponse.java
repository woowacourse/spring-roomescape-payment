package roomescape.service.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.dto.ReservationWithRank;

import java.time.LocalDate;
import java.time.LocalTime;

public record MemberReservationResponse(
        Long reservationId,
        String theme,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul") LocalTime time,
        ReservationStatusResponse reservationStatus,
        MemberPaymentResponse payment
) {

    public static MemberReservationResponse from(ReservationWithRank reservationWithRank) {
        return new MemberReservationResponse(
                reservationWithRank.reservation().getId(),
                reservationWithRank.reservation().getTheme().getName().getValue(),
                reservationWithRank.reservation().getDate(),
                reservationWithRank.reservation().getTime(),
                new ReservationStatusResponse(reservationWithRank.reservation().getStatus().getDescription(), reservationWithRank.rank()),
                MemberPaymentResponse.of(reservationWithRank.reservation().getPayment())
        );
    }
}
