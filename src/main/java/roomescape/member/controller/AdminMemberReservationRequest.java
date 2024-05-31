package roomescape.member.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.service.dto.ReservationCreate;

public record AdminMemberReservationRequest(
        @NotNull(message = "예약 시간 id는 필수 값입니다.")
        @Positive
        Long memberId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        @NotNull(message = "예약 날짜는 필수 값입니다.")
        LocalDate date,

        @NotNull(message = "예약 시간 id는 필수 값입니다.")
        @Positive
        Long timeId,

        @NotNull(message = "테마 id는 필수 값입니다.")
        @Positive
        Long themeId
) {
    public ReservationCreate toReservationCreate() {
        return new ReservationCreate(memberId, timeId, themeId, date);
    }
}
