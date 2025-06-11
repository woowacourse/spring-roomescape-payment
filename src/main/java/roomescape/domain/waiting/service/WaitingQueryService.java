package roomescape.domain.waiting.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.domain.Member;
import roomescape.domain.reservation.domain.Reservation;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.time.domain.ReservationTime;
import roomescape.domain.waiting.domain.Waiting;
import roomescape.domain.waiting.repository.WaitingRepository;
import roomescape.domain.waiting.response.FindAllWaitingResponse;
import roomescape.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class WaitingQueryService {

    private final WaitingRepository waitingRepository;

    public WaitingQueryService(WaitingRepository waitingRepository) {
        this.waitingRepository = waitingRepository;
    }

    public List<FindAllWaitingResponse> findAllWaiting() {
        List<Waiting> allWaiting = waitingRepository.findAll();
        return allWaiting.stream().map(FindAllWaitingResponse::new)
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
