package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.NotFoundException;
import roomescape.model.*;
import roomescape.repository.MemberRepository;
import roomescape.repository.WaitingRepository;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class WaitingReadService {

    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;

    public WaitingReadService(WaitingRepository waitingRepository,
                              MemberRepository memberRepository) {
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
    }

    public boolean existsWaiting(Theme theme, LocalDate date, ReservationTime time) {
        return waitingRepository.existsWaitingByThemeAndDateAndTime(theme, date, time);
    }

    public List<WaitingWithRank> findMemberWaiting(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("해당 id:[%s] 값으로 예약된 예약 대기 내역이 존재하지 않습니다.".formatted(memberId)));
        return waitingRepository.findWaitingWithRankByMemberId(member.getId());
    }

    public List<Waiting> findAllWaiting() {
        return waitingRepository.findAll();
    }

    public Waiting getFirstWaitingByCondition(Theme theme, LocalDate date, ReservationTime time) {
        return waitingRepository.findFirstByThemeAndDateAndTime(theme, date, time)
                .orElseThrow(() ->
                        new NotFoundException("해당 테마:[%s], 날짜:[%s], 시간:[%s] 값으로 예약된 예약 대기 내역이 존재하지 않습니다.".formatted(theme.getName(), date, time.getStartAt())));
    }
}
