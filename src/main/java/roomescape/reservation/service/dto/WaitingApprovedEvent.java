package roomescape.reservation.service.dto;

import roomescape.reservation.domain.Reservation;

public record WaitingApprovedEvent(
        Long reservationId
) {
    public WaitingApprovedEvent (Reservation reservation) {
        this (reservation.getId());
    }
}
