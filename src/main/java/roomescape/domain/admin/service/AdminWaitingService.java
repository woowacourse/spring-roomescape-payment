package roomescape.domain.admin.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.waiting.entity.Waiting;
import roomescape.domain.waiting.exception.WaitingNotFoundException;
import roomescape.domain.waiting.repository.WaitingRepositoryInterface;

@RequiredArgsConstructor
@Service
public class AdminWaitingService {

    private final WaitingRepositoryInterface waitingRepository;

    @Transactional
    public void deleteById(final Long id) {
        waitingRepository.findById(id)
                .ifPresentOrElse(
                        waiting -> waitingRepository.deleteById(id),
                        () -> {
                            throw new WaitingNotFoundException("일치하는 대기 정보가 없어서 삭제할 수 없습니다. id : " + id);
                        }
                );
    }

    @Transactional(readOnly = true)
    public List<Waiting> findAllWaitingReservations() {
        return waitingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean existsByReservation(final Reservation reservation) {
        return waitingRepository.existsByDateAndTimeAndTheme(
                reservation.getDate(),
                reservation.getTime(),
                reservation.getTheme()
        );
    }

    @Transactional(readOnly = true)
    public Waiting findFirstByThemeAndDateAndTimeOrderByIdAsc(final Reservation reservation) {
        return waitingRepository.findFirstByThemeAndDateAndTimeOrderByIdAsc(
                reservation.getTheme(),
                reservation.getDate(),
                reservation.getTime()
        ).orElseThrow(() -> new WaitingNotFoundException("일치하는 대기 정보가 없습니다."));
    }
}
