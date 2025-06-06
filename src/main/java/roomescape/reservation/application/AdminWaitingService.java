package roomescape.reservation.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.resource.AlreadyExistException;
import roomescape.exception.resource.ResourceNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.WaitingRepository;
import roomescape.reservation.infrastructure.projection.WaitingWithRankProjection;
import roomescape.reservation.ui.dto.request.CreateWaitingRequest;
import roomescape.reservation.ui.dto.response.WaitingResponse;
import roomescape.reservation.ui.dto.response.WaitingWithRankResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminWaitingService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    @Transactional
    public WaitingResponse create(final CreateWaitingRequest request) {
        log.info("관리자 예약 대기 생성 요청 - memberId: {}, themeId: {}, timeId: {}, date: {}",
                request.memberId(), request.themeId(), request.timeId(), request.date());

        final ReservationTime time = reservationTimeRepository.getById(request.timeId());
        final Theme theme = themeRepository.getById(request.themeId());
        final Member member = memberRepository.getById(request.memberId());

        final Waiting waiting = createWaiting(request.date(), time, theme, member);

        log.info("예약 대기 생성 완료 - waitingId: {}", waiting.getId());

        return WaitingResponse.from(waiting);
    }

    private Waiting createWaiting(
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        final ReservationSlot reservationSlot = ReservationSlot.of(date, time, theme);
        final Reservation reservation = reservationRepository.findByReservationSlot(reservationSlot)
                .orElseThrow(() -> {
                    log.warn("예약 대기 생성 실패 - 예약 없음 [themeId: {}, date: {}, time: {}]",
                            theme.getId(), date, time.getStartAt());
                    return new ResourceNotFoundException("예약이 없는 상태에서 예약 대기를 추가할 수 없습니다.");
                });

        if (Objects.equals(reservation.getMember(), member)) {
            log.warn("예약 대기 생성 실패 - 본인 예약과 중복 [memberId: {}, reservationId: {}]",
                    member.getId(), reservation.getId());
            throw new AlreadyExistException("해당 예약 슬롯에 본인 예약이 있습니다.");
        }

        if (waitingRepository.existsByReservationSlotAndMember(reservationSlot, member)) {
            log.warn("예약 대기 생성 실패 - 중복 신청 [memberId: {}, themeId: {}, time: {}, date: {}]",
                    member.getId(), theme.getId(), time.getStartAt(), date);
            throw new AlreadyExistException("신청한 예약 대기가 이미 존재합니다.");
        }

        final Waiting waiting = Waiting.of(reservationSlot, member, LocalDateTime.now());

        log.info("예약 대기 저장 - memberId: {}, themeId: {}, time: {}, date: {}",
                member.getId(), theme.getId(), time.getStartAt(), date);

        return waitingRepository.save(waiting);
    }

    @Transactional
    public void deleteAsAdmin(final Long waitingId) {
        log.info("관리자 예약 대기 삭제 요청 - waitingId: {}", waitingId);

        if (!waitingRepository.existsById(waitingId)) {
            log.warn("예약 대기 삭제 실패 - 존재하지 않음 [waitingId: {}]", waitingId);
            throw new ResourceNotFoundException("해당 예약 대기를 찾을 수 없습니다.");
        }

        waitingRepository.deleteById(waitingId);

        log.info("예약 대기 삭제 완료 - waitingId: {}", waitingId);
    }

    @Transactional(readOnly = true)
    public List<WaitingWithRankResponse> findAllWaitingWithRank() {
        log.info("예약 대기 전체 조회 요청");

        final List<WaitingWithRankProjection> projections = waitingRepository.findAllWaitingWithRankProjection();

        log.info("예약 대기 전체 조회 완료 - 개수: {}", projections.size());

        return projections.stream()
                .map(WaitingWithRankResponse::from)
                .toList();
    }
}
