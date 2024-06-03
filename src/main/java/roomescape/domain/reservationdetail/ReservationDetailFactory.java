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
        ReservationTime time = reservationTimeRepository.getReservationTime(timeId);
        Theme theme = themeRepository.getById(themeId);
        return reservationDetailRepository.getReservationDetail(date, time, theme);
    }
}