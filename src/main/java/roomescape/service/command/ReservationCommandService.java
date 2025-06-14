package roomescape.service.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.slot.ReservationTime;
import roomescape.domain.reservation.slot.Theme;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.waiting.ReservationWaitingTicket;
import roomescape.dto.reservation.ReservationCreateCommonRequestDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.exception.DuplicateContentException;
import roomescape.exception.common.NotFoundException;
import roomescape.repository.JpaMemberRepository;
import roomescape.repository.JpaReservationRepository;
import roomescape.repository.JpaReservationTimeRepository;
import roomescape.repository.JpaReservationWaitingTicketRepository;
import roomescape.repository.JpaThemeRepository;

@Service
@Transactional
public class ReservationCommandService {

    private final JpaReservationRepository reservationRepository;
    private final JpaReservationTimeRepository reservationTimeRepository;
    private final JpaThemeRepository themeRepository;
    private final JpaMemberRepository memberRepository;
    private final JpaReservationWaitingTicketRepository waitingTicketRepository;

    public ReservationCommandService(JpaReservationRepository reservationRepository,
                                     JpaReservationTimeRepository reservationTimeRepository,
                                     JpaThemeRepository themeRepository, JpaMemberRepository memberRepository,
                                     JpaReservationWaitingTicketRepository waitingTicketRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingTicketRepository = waitingTicketRepository;
    }

    public ReservationResponseDto bookReservation(ReservationCreateCommonRequestDto request) {
        ReservationTime reservationTime = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new NotFoundException("예약 시간", request.timeId()));

        validateDuplicate(request.date(), request.timeId(), request.themeId());
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NotFoundException("테마", request.themeId()));
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundException("유저", request.memberId()));
        Reservation requestReservation = new Reservation(member, request.date(), reservationTime, theme, ReservationStatus.RESERVED);
        requestReservation.validateReservableTime(LocalDateTime.now());

        Reservation newReservation = reservationRepository.save(requestReservation);

        return ReservationResponseDto.of(newReservation);
    }

    public void cancelReservationBy(Long id) {
         Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("예약", id));
        reservationRepository.deleteById(id);
        updateReservationWaitingToReservation(reservation);
    }

    private void validateDuplicate(LocalDate date, long timeId, long themeId) {
        List<Reservation> reservations = reservationRepository.findReservationsByDateAndTimeIdAndThemeIdAndStatus(
                date, timeId, themeId, ReservationStatus.RESERVED);
        if (!reservations.isEmpty()) {
            throw new DuplicateContentException("이미 예약이 존재합니다. 예약 대기 기능을 사용해주세요.", String.format(
                    "date : %s, timeId : %s, themeId : %s", date.toString(), timeId, themeId
            ));
        }
    }

    private void updateReservationWaitingToReservation(Reservation deletedReservation) {
        List<Reservation> reservationWaitings = reservationRepository.findReservationsByDateAndTimeIdAndThemeIdAndStatus(
                deletedReservation.getDate(),
                deletedReservation.getTime().getId(),
                deletedReservation.getTheme().getId(),
                ReservationStatus.WAITING
        );

        if (reservationWaitings.isEmpty()) {
            return;
        }

        Reservation earliestWaiting = reservationWaitings.stream()
                .map(waiting -> waitingTicketRepository.findByReservationId(waiting.getId())
                        .orElseThrow(() -> new NotFoundException("예약 대기표", waiting.getId())))
                .min(Comparator.comparing(ReservationWaitingTicket::getCreatedAt))
                .orElseThrow(() -> new NotFoundException("첫번째 예약 대기표를 찾을 수 없습니다."))
                .getReservation();

        waitingTicketRepository.deleteByReservationId(earliestWaiting.getId());
        earliestWaiting.changeStatusToReserved();
    }
}
