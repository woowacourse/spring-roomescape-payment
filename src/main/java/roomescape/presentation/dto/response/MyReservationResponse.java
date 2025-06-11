package roomescape.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Reservation;
import roomescape.domain.Waiting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Schema(description = "내 예약 정보 응답 DTO")
public record MyReservationResponse(
        @Schema(description = "예약 ID")
        Long id,

        @Schema(description = "테마 이름", example = "공포의 방탈출")
        String theme,

        @Schema(description = "예약 일자", example = "2024-03-20")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "예약 시간", example = "14:00")
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,

        @Schema(description = "예약 상태", example = "예약 완료")
        String status,

        @Schema(description = "대기 여부")
        boolean isWaiting
        ) {

    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getName(),
                false
        );
    }

    public static MyReservationResponse from(Waiting waiting) {
        return new MyReservationResponse(
                waiting.getId(),
                waiting.getReservationInfo().getTheme().getName(),
                waiting.getReservationInfo().getDate(),
                waiting.getReservationInfo().getTime().getStartAt(),
                waiting.getRank() + "번째 예약대기",
                true
        );
    }

    public static List<MyReservationResponse> from(List<Reservation> reservations, List<Waiting> waitings) {
        return Stream.concat(
                reservations.stream().map(MyReservationResponse::from),
                waitings.stream().map(MyReservationResponse::from))
        .sorted(Comparator.comparing(MyReservationResponse::date)
                .thenComparing(MyReservationResponse::time))
        .toList();
    }
}
