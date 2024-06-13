package roomescape.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.domain.Theme;
import roomescape.domain.TimeSlot;

@Schema(description = "Reservation Request Model")
public record ReservationRequest(@Schema(description = "Member ID", example = "1")
                                 Long memberId,

                                 @Schema(description = "Reservation date", example = "2023-12-25")
                                 LocalDate date,

                                 @Schema(description = "Time Slot ID", example = "1")
                                 Long timeId,

                                 @Schema(description = "Theme ID", example = "1")
                                 Long themeId) {

    public ReservationRequest {
        isValid(memberId, date, timeId, themeId);
    }

    public static ReservationRequest from(Long memberId, MemberReservationRequest memberReservationRequest) {
        return new ReservationRequest(memberId, memberReservationRequest.date(),
                memberReservationRequest.timeId(), memberReservationRequest.themeId());
    }

    public Reservation toEntity(Member member, TimeSlot time, Theme theme) {
        return new Reservation(member, date, time, theme, ReservationStatus.BOOKING);
    }

    private void isValid(Long memberId, LocalDate date, Long timeId, Long themeId) {
        if (memberId == null) {
            throw new IllegalArgumentException("[ERROR] 예약자는 비워둘 수 없습니다.");
        }

        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 예약 날짜입니다.");
        }

        if (timeId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 예약 시간입니다.");
        }

        if (themeId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 테마 입니다.");
        }
    }
}
