package roomescape.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import roomescape.business.dto.UserReservationDetailDto;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.vo.ReservationStatus;

public record ReservationMineResponse(
        String id,
        String themeName,
        LocalDate date,
        LocalTime time,
        ReservationStatus reservationStatus,
        Long aheadCount,
        String paymentKey,
        Long amount
) {
    public static ReservationMineResponse from(UserReservationDetailDto userReservationDetailDto) {
        Reservation reservation = userReservationDetailDto.reservation();
        return new ReservationMineResponse(
                reservation.getId().value(),
                reservation.getTheme().getName().value(),
                reservation.getDate().value(),
                reservation.getTime().getStartTime().value(),
                reservation.getReservationStatus(),
                userReservationDetailDto.aheadCount(),
                userReservationDetailDto.paymentKey(),
                userReservationDetailDto.totalAmount()
        );
    }

    public static List<ReservationMineResponse> from(List<UserReservationDetailDto> userReservationDetailDtos) {
        return userReservationDetailDtos.stream()
                .map(ReservationMineResponse::from)
                .toList();
    }
}
