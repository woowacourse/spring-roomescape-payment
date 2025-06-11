package roomescape.business.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginInfo;
import roomescape.business.model.entity.Member;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.entity.Waiting;
import roomescape.business.model.vo.Id;
import roomescape.exception.member.MemberNotFoundException;
import roomescape.exception.reservation.ThemeNotFoundException;
import roomescape.exception.reservation.TimeSlotNotFoundException;
import roomescape.exception.reservation.WaitingNotFoundException;
import roomescape.infrastructure.MemberRepository;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.infrastructure.ThemeRepository;
import roomescape.infrastructure.WaitingRepository;
import roomescape.presentation.dto.request.WaitingRequest;
import roomescape.presentation.dto.response.WaitingResponse;
import roomescape.presentation.dto.response.WaitingWithRankResponse;

@Service
@AllArgsConstructor
@Transactional
public class WaitingService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final WaitingRepository waitingRepository;

    public WaitingResponse createWaiting(LoginInfo loginInfo, WaitingRequest request) {
        Member member = memberRepository.findById(Id.create(loginInfo.id()))
                .orElseThrow(MemberNotFoundException::new);
        TimeSlot time = reservationTimeRepository.findById(Id.create(request.timeId()))
                .orElseThrow(TimeSlotNotFoundException::new);
        Theme theme = themeRepository.findById(Id.create(request.themeId()))
                .orElseThrow(ThemeNotFoundException::new);

        Waiting waiting = Waiting.create(member, request.date(), time, theme);
        waitingRepository.save(waiting);
        return WaitingResponse.from(waiting);
    }

    @Transactional(readOnly = true)
    public List<WaitingResponse> findAllWaitings() {
        return waitingRepository.findAll()
                .stream()
                .map(WaitingResponse::from)
                .toList();
    }

    @Transactional
    public void deleteWaitingById(String id) {
        Waiting waiting = waitingRepository.findById(Id.create(id))
                .orElseThrow(WaitingNotFoundException::new);
        waitingRepository.delete(waiting);
    }


    public List<WaitingWithRankResponse> getMyWaitings(String userId) {
        Member member = memberRepository.findById(Id.create(userId))
                .orElseThrow(MemberNotFoundException::new);
        return waitingRepository.findByUserIdWithRank(member.getId());
    }
}

