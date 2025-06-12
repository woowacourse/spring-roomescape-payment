package roomescape.service;

import static roomescape.domain.Reservation.validateReservableTime;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.member.Member;
import roomescape.domain.waiting.Waiting;
import roomescape.domain.waiting.WaitingWithRank;
import roomescape.dto.reservation.MyReservationAndWaitingsResponse;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.dto.waiting.WaitingCreateRequest;
import roomescape.dto.waiting.WaitingResponse;
import roomescape.exception.NotFoundException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.repository.WaitingQueryRepository;
import roomescape.repository.WaitingRepository;

@Service
public class WaitingService {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationService.class);

    private final WaitingRepository waitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingQueryRepository waitingQueryRepository;
    private final ReservationService reservationService;

    public WaitingService(final WaitingRepository WaitingRepository,
                          final ReservationTimeRepository ReservationTimeRepository,
                          final ThemeRepository themeRepository,
                          final MemberRepository memberRepository,
                          final WaitingQueryRepository waitingQueryRepository,
                          final ReservationService reservationService) {
        this.waitingRepository = WaitingRepository;
        this.reservationTimeRepository = ReservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingQueryRepository = waitingQueryRepository;
        this.reservationService = reservationService;
    }

    public WaitingResponse createWaiting(WaitingCreateRequest request) {
        ReservationTime ReservationTime = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new NotFoundException("[ERROR] 예약 시간을 찾을 수 없습니다. id : " + request.timeId()));

        validateReservableTime(request.date(), ReservationTime.getStartAt());

        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NotFoundException("[ERROR] 테마를 찾을 수 없습니다. id : " + request.themeId()));

        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundException("[ERROR] 유저를 찾을 수 없습니다. id : " + request.memberId()));

        Waiting requestWaiting = Waiting.createWithoutId(request.date(), member, theme, ReservationTime);
        Waiting newWaiting = waitingRepository.save(requestWaiting);
        LOG.info("예약 대기 생성 성공: {}", newWaiting.getId());
        return WaitingResponse.from(newWaiting);
    }

    public List<WaitingResponse> findAllWaitings() {
        List<Waiting> waitings = waitingRepository.findAll();

        return waitings.stream()
                .map(WaitingResponse::from)
                .toList();
    }

    public void deleteWaiting(Long id) {
        if (!waitingRepository.existsById(id)) {
            throw new NotFoundException("[ERROR] 등록된 예약 대기 번호만 삭제할 수 있습니다. 입력된 번호는 " + id + "입니다.");
        }
        waitingRepository.deleteById(id);
        LOG.info("예약 대기 삭제 성공: {}", id);
    }

    public List<MyReservationAndWaitingsResponse> findMyWaitings(Long id) {
        List<WaitingWithRank> waitingsWithRank = waitingQueryRepository.findWaitingsWithRankByMemberId(id);
        return waitingsWithRank.stream()
                .map(MyReservationAndWaitingsResponse::from)
                .toList();
    }

    public void approveWaiting(final Long id) {
        Waiting waiting = waitingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[ERROR] 등록된 예약 대기 번호만 승인할 수 있습니다. 입력된 번호는 " + id + "입니다."));
        reservationService.validateDuplicate(waiting.getDate(), waiting.getTime().getId(), waiting.getTheme().getId());

        waitingRepository.deleteById(id);
        reservationService.createReservation(ReservationCreateRequest.from(waiting));
        LOG.info("예약 대기의 예약 확정 성공: {}(삭제됨)", id);
    }
}
