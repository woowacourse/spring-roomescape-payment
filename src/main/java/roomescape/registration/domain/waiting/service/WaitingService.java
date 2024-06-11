package roomescape.registration.domain.waiting.service;

import org.springframework.stereotype.Service;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.MemberExceptionCode;
import roomescape.exception.model.ReservationExceptionCode;
import roomescape.exception.model.WaitingExceptionCode;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.repository.ReservationRepository;
import roomescape.registration.domain.waiting.domain.Waiting;
import roomescape.registration.domain.waiting.domain.WaitingWithRank;
import roomescape.registration.domain.waiting.dto.WaitingRequest;
import roomescape.registration.domain.waiting.dto.WaitingResponse;
import roomescape.registration.domain.waiting.repository.WaitingRepository;
import roomescape.registration.dto.RegistrationDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    public WaitingService(WaitingRepository waitingRepository, ReservationRepository reservationRepository, MemberRepository memberRepository) {
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

        Waiting unSavedWaiting = new Waiting(reservation, LocalDateTime.now());

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
        Waiting waiting = waitingRepository.findByReservationDateAndReservationThemeIdAndReservationReservationTimeIdAndReservationMemberId(
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

    public void removeWaiting(long waitingId, Long memberId) {
        Waiting waiting = waitingRepository.findById(waitingId)
                .orElseThrow(() -> new RoomEscapeException(WaitingExceptionCode.WAITING_NOT_EXIST_EXCEPTION));
        Reservation reservation = reservationRepository.findById(waiting.getId())
                .orElseThrow(() -> new RoomEscapeException(ReservationExceptionCode.RESERVATION_NOT_EXIST));
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new RoomEscapeException(MemberExceptionCode.MEMBER_NOT_EXIST_EXCEPTION));

        if(!reservation.getMember().equals(member)) {
            throw new RoomEscapeException(WaitingExceptionCode.ONLY_OWNER_CAN_DELETE);
        }
        waitingRepository.delete(waiting);
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
        boolean existWaiting = waitingRepository.existsByReservationDateAndReservationThemeIdAndReservationReservationTimeIdAndReservationMemberId(
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
