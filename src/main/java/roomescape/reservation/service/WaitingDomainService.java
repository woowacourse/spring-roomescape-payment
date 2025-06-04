package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.util.time.DateTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.exception.MemberNotFound;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.WaitingRepository;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.dto.ReservationResponse;
import roomescape.reservation.presentation.dto.WaitingResponse;
import roomescape.reservationTime.domain.ReservationTimeRepository;
import roomescape.theme.domain.ThemeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class WaitingDomainService {

    private final DateTime dateTime;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;

    public WaitingDomainService(
        final DateTime dateTime,
        final ReservationRepository reservationRepository,
        final MemberRepository memberRepository,
        final WaitingRepository waitingRepository
    ) {
        this.dateTime = dateTime;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
    }

    @Transactional
    public WaitingResponse createWaiting(final ReservationRequest request, final Long memberId) {
        Reservation reservation = reservationRepository.findBy(request.date(), request.timeId(), request.themeId())
            .orElseThrow(() -> new ReservationException("예약 정보가 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFound("멤버를 찾을 수 없습니다."));

        validateNotReservationOwner(reservation, member);
        validateCanReserveDateTime(reservation, dateTime.now());
        validateDuplicateWaiting(reservation, member);

        Waiting waiting = waitingRepository.save(Waiting.createWithoutId(reservation, member));
        return WaitingResponse.from(waiting);
    }

    private void validateNotReservationOwner(final Reservation reservation, final Member member) {
        if (reservation.isSameMember(member)) {
            throw new ReservationException("예약자는 예약대기를 할 수 없습니다.");
        }
    }

    private void validateCanReserveDateTime(final Reservation reservation, final LocalDateTime now) {
        if (reservation.isCannotReserveDateTime(now)) {
            throw new ReservationException("예약할 수 없는 날짜와 시간입니다.");
        }
    }

    private void validateDuplicateWaiting(Reservation reservation, Member member) {
        if (waitingRepository.existsByReservationIdAndMemberId(reservation.getId(), member.getId())) {
            throw new ReservationException("이미 예약대기 중입니다.");
        }
    }

    @Transactional
    public void deleteWaiting(final Long waitingId) {
        waitingRepository.findById(waitingId)
            .orElseThrow(() -> new ReservationException("예약 대기를 찾을 수 없습니다."));

        waitingRepository.deleteById(waitingId);
    }

    public List<ReservationResponse> findAllWaitings() {
        List<Waiting> waitings = waitingRepository.findAll();

        return waitings.stream()
            .map(ReservationResponse::from)
            .toList();
    }
}
