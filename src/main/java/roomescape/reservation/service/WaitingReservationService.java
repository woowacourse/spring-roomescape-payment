package roomescape.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.RoomEscapeInformation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.WaitingReservationRequest;
import roomescape.reservation.dto.WaitingReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.RoomEscapeInformationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;

@Service
@RequiredArgsConstructor
public class WaitingReservationService {

    private final ReservationRepository reservationRepository;
    private final WaitingReservationRepository waitingReservationRepository;
    private final MemberRepository memberRepository;
    private final RoomEscapeInformationRepository roomEscapeInformationRepository;

    @Transactional
    public WaitingReservationResponse registerWaitingReservation(final WaitingReservationRequest request, final LoginMember loginMember) {
        final Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다."));
        final RoomEscapeInformation roomEscapeInformation = roomEscapeInformationRepository.findByDateAndTimeIdAndThemeId(
                        request.date(), request.timeId(), request.themeId())
                .orElseThrow(() -> new NotFoundException("방탈출 정보가 존재하지 않습니다."));

        final WaitingReservation waitingReservation = WaitingReservation.builder()
                .roomEscapeInformation(roomEscapeInformation)
                .member(member)
                .build();
        return new WaitingReservationResponse(waitingReservationRepository.save(waitingReservation));
    }

    @Transactional
    public void denyWaitingByIdForAdmin(Long waitingId) {
        WaitingReservation waiting = getWaitingById(waitingId);
        waitingReservationRepository.delete(waiting);
        deleteRoomEscapeInfoIfEmpty(waiting.getRoomEscapeInformation());
    }

    @Transactional
    public void cancelWaitingByIdForMember(Long waitingId, LoginMember loginMember) {
        WaitingReservation waiting = getWaitingById(waitingId);
        validateCancelPermission(loginMember, waiting);

        waitingReservationRepository.delete(waiting);
        deleteRoomEscapeInfoIfEmpty(waiting.getRoomEscapeInformation());
    }

    private void validateCancelPermission(LoginMember loginMember, WaitingReservation waiting) {
        boolean notSameMember = !waiting.isOwnedBy(loginMember.id());
        if (notSameMember) {
            throw new ReservationException("자신의 예약만 삭제할 수 있습니다.");
        }
    }

    private WaitingReservation getWaitingById(Long waitingId) {
        return waitingReservationRepository.findById(waitingId)
                .orElseThrow(() -> new NotFoundException("Waiting을 찾지 못했습니다, waitingId: " + waitingId));
    }

    private void deleteRoomEscapeInfoIfEmpty(RoomEscapeInformation roomEscapeInformation) {
        Long infoId = roomEscapeInformation.getId();

        boolean hasBooked = reservationRepository.existsByRoomEscapeInformationId(infoId);
        boolean hasWaiting = waitingReservationRepository.existsByRoomEscapeInformationId(infoId);
        if (!hasBooked && !hasWaiting) {
            roomEscapeInformationRepository.deleteById(infoId);
        }
    }
}
