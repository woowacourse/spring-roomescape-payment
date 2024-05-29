package roomescape.reservation.service.dto;

import roomescape.reservation.controller.dto.MemberReservationRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MemberReservationCreate(long memberId, long themeId, long timeId, String paymentKey, String orderId,
                                      BigDecimal amount,
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

    public ReservationCreate toReservationCreate() {
        return new ReservationCreate(
                timeId,
                themeId,
                memberId,
                date
        );
    }
}

