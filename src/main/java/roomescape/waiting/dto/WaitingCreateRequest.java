package roomescape.waiting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.Waiting;

public record WaitingCreateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "예약 날짜", example = "2024-05-30")
        LocalDate date,
        @Schema(description = "예약 테마 id", example = "1")
        Long themeId,
        @Schema(description = "예약 시간 id", example = "1")
        Long timeId) {
    public Waiting createWaiting(Reservation reservation, Member waitingMember) {
        return new Waiting(reservation, waitingMember);
    }
}
