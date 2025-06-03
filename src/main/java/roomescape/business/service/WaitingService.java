package roomescape.business.service;

import static roomescape.exception.ErrorCode.RESERVATION_DUPLICATED;

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
import roomescape.exception.ErrorCode;
import roomescape.exception.business.DuplicatedException;
import roomescape.exception.business.NotFoundException;
import roomescape.infrastructure.MemberRepository;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.infrastructure.ThemeRepository;
import roomescape.infrastructure.WaitingRepository;
import roomescape.presentation.dto.request.WaitingRequest;
import roomescape.presentation.dto.response.WaitingResponse;
import roomescape.presentation.dto.response.WaitingWithRankReponse;

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
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_EXIST));
        TimeSlot time = reservationTimeRepository.findById(Id.create(request.timeId()))
                .orElseThrow(() -> new NotFoundException(ErrorCode.RESERVATION_TIME_NOT_EXIST));
        Theme theme = themeRepository.findById(Id.create(request.themeId()))
                .orElseThrow(() -> new NotFoundException(ErrorCode.THEME_NOT_EXIST));

        if (reservationRepository.existsByDate_ValueAndTimeSlot_StartAtAndThemeId(request.date(),
                time.getStartAt(),
                theme.getId())) {
            throw new DuplicatedException(RESERVATION_DUPLICATED);
        }
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
                .orElseThrow(() -> new NotFoundException(ErrorCode.WAITING_NOT_EXIST));
        waitingRepository.delete(waiting);
    }


    public List<WaitingWithRankReponse> getMyWaitings(String userId) {
        Member member = memberRepository.findById(Id.create(userId))
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_EXIST));
        return waitingRepository.findByUserIdWithRank(member.getId());
    }
}

