package roomescape.domain.reservationdetail;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationDetailFactory {
    private final ReservationDetailRepository reservationDetailRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationDetail createReservationDetail(LocalDate date, Long timeId, Long themeId) {
        ReservationTime time = reservationTimeRepository.getById(timeId);
        Theme theme = themeRepository.getById(themeId);
        return reservationDetailRepository.findByDateAndTimeAndTheme(date, time, theme)
                .orElseGet(() -> reservationDetailRepository.save(new ReservationDetail(date, time, theme)));
    }
}
