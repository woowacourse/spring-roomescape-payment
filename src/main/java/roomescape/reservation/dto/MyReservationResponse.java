package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import roomescape.reservation.repository.dto.MemberRegistrationProjection;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        int rank
) {
    public static MyReservationResponse from(MemberRegistrationProjection projection) {
        return new MyReservationResponse(
                projection.getId(),
                projection.getThemeName(),
                projection.getDate(),
                projection.getTime(),
                RegistrationStatus.from(projection.getType()).getOutput(),
                projection.getRank()
        );
    }

    public static List<MyReservationResponse> from(List<MemberRegistrationProjection> projections) {
        return projections.stream()
                .map(MyReservationResponse::from)
                .toList();
    }
}
