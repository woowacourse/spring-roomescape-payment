package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.reservation.slot.*;
import roomescape.exception.RoomEscapeBusinessException;
import roomescape.service.dto.ReservationSlotRequest;

import java.time.LocalDate;

@Service
public class ReservationSlotService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationSlotService(ReservationTimeRepository reservationTimeRepository,
                                  ThemeRepository themeRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    public ReservationSlot findSlot(ReservationSlotRequest reservationSlotRequest) {
        LocalDate date = reservationSlotRequest.date();
        ReservationTime time = findTimeById(reservationSlotRequest.timeId());
        Theme theme = findThemeById(reservationSlotRequest.themeId());

        return new ReservationSlot(date, time, theme);
    }

    private Theme findThemeById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeBusinessException("존재하지 않는 테마입니다."));
    }

    private ReservationTime findTimeById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeBusinessException("존재하지 않는 예약 시간입니다."));
    }
}
