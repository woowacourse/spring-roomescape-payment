package roomescape.waiting.service;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.WaitingExceptionCode;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingWithRank;
import roomescape.waiting.dto.WaitingRequest;
import roomescape.waiting.dto.WaitingResponse;
import roomescape.waiting.repository.WaitingRepository;
import roomescape.reservation.dto.RegistrationDto;

@Tag(name = "예약 대기 서비스", description = "예약 대기 저장, 예약 대기 검색 등 예약 대기와 관련된 로직을 수행한다.")
@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    public WaitingService(WaitingRepository waitingRepository, ReservationRepository reservationRepository,
                          MemberRepository memberRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
    }

    public WaitingResponse addWaiting(WaitingRequest waitingRequest, long memberId) {
        validateAlreadyReservation(waitingRequest, memberId);
        validateAlreadyWaiting(waitingRequest, memberId);

        Reservation reservation = reservationRepository.findReservationByDateAndThemeIdAndReservationTimeId(
                waitingRequest.date(),
                waitingRequest.themeId(),
                waitingRequest.timeId()
        );
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new RoomEscapeException(WaitingExceptionCode.MEMBER_INFO_IS_NULL_EXCEPTION));

        Waiting unSavedWaiting = new Waiting(reservation, member, LocalDateTime.now());

        return WaitingResponse.from(waitingRepository.save(unSavedWaiting));
    }

    public List<WaitingResponse> findWaitings() {
        List<Waiting> waitings = waitingRepository.findAll();

        return waitings.stream()
                .map(WaitingResponse::from)
                .toList();
    }

    public Waiting findWaitingById(long id) {
        return waitingRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeException(WaitingExceptionCode.WAITING_NOT_EXIST_EXCEPTION));
    }

    public List<WaitingWithRank> findMemberWaitingWithRank(long memberId) {
        return waitingRepository.findWaitingsWithRankByMemberId(memberId);
    }

    public long countWaitingRank(RegistrationDto registrationDto) {
        Waiting waiting = waitingRepository.findByReservationDateAndReservationThemeIdAndReservationReservationTimeIdAndMemberId(
                registrationDto.date(),
                registrationDto.themeId(),
                registrationDto.timeId(),
                registrationDto.memberId()
        );

        return waitingRepository.countWaitingRankByDateAndThemeIdAndReservationTimeId(
                waiting.getId(),
                registrationDto.date(),
                registrationDto.themeId(),
                registrationDto.timeId()
        );
    }

    public void removeWaiting(long waitingId) {
        waitingRepository.deleteById(waitingId);
    }

    private void validateAlreadyReservation(WaitingRequest waitingRequest, long memberId) {
        boolean existReservation = reservationRepository.existsByDateAndThemeIdAndReservationTimeIdAndMemberId(
                waitingRequest.date(),
                waitingRequest.themeId(),
                waitingRequest.timeId(),
                memberId
        );

        if (existReservation) {
            throw new RoomEscapeException(WaitingExceptionCode.ALREADY_REGISTRATION_EXCEPTION);
        }
    }

    private void validateAlreadyWaiting(WaitingRequest waitingRequest, long memberId) {
        boolean existWaiting = waitingRepository.existsByReservationDateAndReservationThemeIdAndReservationReservationTimeIdAndMemberId(
                waitingRequest.date(),
                waitingRequest.themeId(),
                waitingRequest.timeId(),
                memberId
        );

        if (existWaiting) {
            throw new RoomEscapeException(WaitingExceptionCode.ALREADY_REGISTRATION_EXCEPTION);
        }
    }
}
