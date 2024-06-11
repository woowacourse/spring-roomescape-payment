package roomescape.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.model.ReservationTime;

import java.time.LocalTime;

public class ReservationTimeResponse {

    @Schema(description = "예약 시간 ID", example = "1")
    private Long id;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "예약 시간", example = "10:00")
    private LocalTime startAt;

    public ReservationTimeResponse() {
    }

    public ReservationTimeResponse(Long id, LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    public static ReservationTimeResponse of(ReservationTime domain) {
        return new ReservationTimeResponse(domain.getId(), domain.getStartAt());
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }
}
