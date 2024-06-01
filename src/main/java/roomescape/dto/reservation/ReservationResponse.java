package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.member.MemberResponse;
import roomescape.dto.reservationtime.ReservationTimeResponse;
import roomescape.dto.theme.ThemeResponse;

public record ReservationResponse(
        Long id,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        MemberResponse member
) {

    public static ReservationResponse from(Reservation reservation) {
        ReservationTimeResponse reservationTimeResponse = createReservationTimeResponse(reservation.getTime());
        ThemeResponse themeResponse = createThemeResponse(reservation.getTheme());
        MemberResponse memberResponse = createMemberResponse(reservation.getMember());

        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                reservationTimeResponse,
                themeResponse,
                memberResponse
        );
    }

    private static ReservationTimeResponse createReservationTimeResponse(ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt()
        );
    }

    private static ThemeResponse createThemeResponse(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getThemeName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }

    private static MemberResponse createMemberResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getRole()
        );
    }
}
