package roomescape.reservation.repository.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public interface MemberRegistrationProjection {
    Long getId();
    String getType();
    String getThemeName();
    LocalDate getDate();
    LocalTime getTime();
    int getRank();
}
