package roomescape.reservation.service.dto;

import java.time.LocalDate;
import roomescape.reservation.controller.dto.MemberReservationRequest;

public record MemberReservationCreate(long memberId, long themeId, long timeId, String paymentKey, String orderId,
                                      long amount,
                                      LocalDate date) {

    public static MemberReservationCreate from(MemberReservationRequest memberReservationRequest) {
        return new MemberReservationCreate(
                memberReservationRequest.memberId(),
                memberReservationRequest.themeId(),
                memberReservationRequest.timeId(),
                memberReservationRequest.paymentKey(),
                memberReservationRequest.orderId(),
                memberReservationRequest.amount(),
                memberReservationRequest.date()
        );
    }
}

