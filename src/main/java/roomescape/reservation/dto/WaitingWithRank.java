package roomescape.reservation.dto;

import roomescape.reservation.domain.Reservation;

public record WaitingWithRank(Reservation waiting, int rank) {
}
