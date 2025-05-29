package roomescape.application.reservation.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.reservation.query.dto.WaitingResult;
import roomescape.application.reservation.query.dto.WaitingWithRankResult;
import roomescape.domain.reservation.repository.WaitingRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class WaitingQueryService {

    private final WaitingRepository waitingRepository;

    public WaitingQueryService(final WaitingRepository waitingRepository) {
        this.waitingRepository = waitingRepository;
    }

    public List<WaitingWithRankResult> findWaitingByMemberId(final Long memberId) {
        return waitingRepository.findWaitingsWithRankByMemberId(memberId)
                .stream()
                .map(w -> WaitingWithRankResult.from(w.waiting(), oneBasedRank(w.rank())))
                .toList();
    }

    private long oneBasedRank(final long zeroBasedRank) {
        return zeroBasedRank + 1;
    }

    public List<WaitingResult> findAll() {
        return waitingRepository.findAllWithMemberAndThemeAndTime()
                .stream()
                .map(WaitingResult::from)
                .toList();
    }
}
