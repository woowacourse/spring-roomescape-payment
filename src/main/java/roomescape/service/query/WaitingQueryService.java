package roomescape.service.query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.dto.response.WaitingResponse;
import roomescape.exception.NotFoundException;
import roomescape.repository.WaitingRepository;

@Service
@Transactional(readOnly = true)
public class WaitingQueryService {

    private final WaitingRepository waitingRepository;

    public WaitingQueryService(WaitingRepository waitingRepository) {
        this.waitingRepository = waitingRepository;
    }

    public List<WaitingResponse> findAllWaiting() {
        List<Waiting> allWaiting = waitingRepository.findAll();
        return allWaiting.stream().map(WaitingResponse::new)
                .toList();
    }

    public Optional<Waiting> findFirstWaitingByReservation(Reservation deletedReservation) {
        return waitingRepository.findFirstWaiting(
                deletedReservation.getTheme().getId(),
                deletedReservation.getDate(),
                deletedReservation.getReservationTime().getId());
    }

    public Waiting getWaitingById(long waitingId) {
        return waitingRepository.findById(waitingId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 대기가 존재하지 않습니다."));
    }

    public boolean existsAlreadyWaiting(Theme theme, LocalDate date, ReservationTime time, Member member) {
        return waitingRepository.existsDuplicated(theme.getId(), date, time.getId(), member.getId());
    }

    public boolean existsWaitingInTime(ReservationTime time) {
        return waitingRepository.existsByTime(time);
    }

    public boolean existsWaitingInTheme(Theme theme) {
        return waitingRepository.existsByTheme(theme);
    }
}
