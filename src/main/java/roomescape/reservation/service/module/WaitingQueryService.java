package roomescape.reservation.service.module;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.WaitingWithRank;
import roomescape.reservation.repository.WaitingRepository;

@Service
public class WaitingQueryService {

    private final WaitingRepository waitingRepository;

    public WaitingQueryService(WaitingRepository waitingRepository) {
        this.waitingRepository = waitingRepository;
    }

    public Waiting save(Waiting waiting) {
        return waitingRepository.save(waiting);
    }

    public List<Waiting> findWaitings() {
        return waitingRepository.findAllByStatus(Status.WAIT);
    }

    public Waiting findById(Long id) {
        return waitingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약 대기 내역이 없습니다."));
    }

    public List<WaitingWithRank> findWaitingWithRanksByMemberId(Long memberId) {
        return waitingRepository.findAllByMemberId(memberId)
                .stream()
                .map(this::createWaitingWithRank)
                .toList();
    }

    private WaitingWithRank createWaitingWithRank(Waiting waiting) {
        Long rank = waitingRepository.countRankBySameWaiting(
                waiting.getTheme(),
                waiting.getDate(),
                waiting.getTime(),
                waiting.getId()
        );
        return new WaitingWithRank(waiting, rank);
    }

    public void delete(Long id) {
        waitingRepository.deleteById(id);
    }
}
