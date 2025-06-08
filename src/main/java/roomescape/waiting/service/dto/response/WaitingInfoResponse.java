package roomescape.waiting.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.waiting.domain.Waiting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(name = "WaitingInfoResponse(예약 대기 정보 응답 DTO)")
public record WaitingInfoResponse(
        Long id,
        String member,
        String theme,
        LocalDate date,
        LocalTime time,
        LocalDateTime createdAt
) {
    public static WaitingInfoResponse from(Waiting waiting) {
        return new WaitingInfoResponse(
                waiting.getId(),
                waiting.getMember().getName(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                waiting.getCreatedAt()
        );
    }
}
