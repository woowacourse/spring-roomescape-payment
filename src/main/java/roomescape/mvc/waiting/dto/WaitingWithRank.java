package roomescape.mvc.waiting.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public interface WaitingWithRank {

    long getId();

    LocalDate getDate();

    String getThemeName();

    LocalTime getStartAt();

    String getMemberName();

    long getRank();

    String getPaymentKey();

    Long getAmount();
}
