package roomescape.admin.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.DataNotFoundException;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepositoryInterface;

@RequiredArgsConstructor
@Service
public class AdminWaitingService {

    private final WaitingRepositoryInterface waitingRepository;

    @Transactional
    public void deleteWaitingById(final Long id) {
        waitingRepository.findById(id)
                .ifPresentOrElse(
                        waiting -> waitingRepository.deleteById(id),
                        () -> {
                            throw new DataNotFoundException("해당 대기 데이터가 존재하지 않습니다. id = " + id);
                        }
                );
    }

    @Transactional(readOnly = true)
    public List<Waiting> findAllWaitingReservations() {
        return waitingRepository.findAll();
    }
}
