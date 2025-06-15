package roomescape.service.command;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.slot.ReservationTime;
import roomescape.domain.reservation.slot.Theme;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.waiting.ReservationWaitingTicket;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.reservation.ReservationCreateCommonRequestDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.exception.AccessDeniedException;
import roomescape.exception.ReservationWaitingForbiddenException;
import roomescape.exception.common.NotFoundException;
import roomescape.repository.JpaMemberRepository;
import roomescape.repository.JpaReservationRepository;
import roomescape.repository.JpaReservationTimeRepository;
import roomescape.repository.JpaReservationWaitingTicketRepository;
import roomescape.repository.JpaThemeRepository;

@Service
@Transactional
public class ReservationWaitingCommandService {

    private final JpaReservationWaitingTicketRepository reservationWaitingTicketRepository;
    private final JpaReservationRepository reservationRepository;
    private final JpaReservationTimeRepository reservationTimeRepository;
    private final JpaThemeRepository themeRepository;
    private final JpaMemberRepository memberRepository;
    private final Clock clock;

    public ReservationWaitingCommandService(JpaReservationWaitingTicketRepository reservationWaitingTicketRepository,
                                            JpaReservationRepository reservationRepository,
                                            JpaReservationTimeRepository reservationTimeRepository,
                                            JpaThemeRepository themeRepository, JpaMemberRepository memberRepository,
                                            Clock clock) {
        this.reservationWaitingTicketRepository = reservationWaitingTicketRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.clock = clock;
    }

    public ReservationResponseDto createReservationWaiting(ReservationCreateCommonRequestDto request) {
        ReservationTime reservationTime = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new NotFoundException("예약 시간", request.timeId()));
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NotFoundException("테마", request.themeId()));
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundException("유저", request.memberId()));

        List<Reservation> alreadyBookedReservations = reservationRepository.findReservationsByDateAndTimeIdAndThemeId(
                request.date(),
                request.timeId(),
                request.themeId());
        validateReservationWaitingAvailable(alreadyBookedReservations, member);

        Reservation requestReservation = new Reservation(member, request.date(), reservationTime, theme, ReservationStatus.WAITING);
        requestReservation.validateReservableTime(LocalDateTime.now(clock));

        Reservation newReservation = reservationRepository.save(requestReservation);
        reservationWaitingTicketRepository.save(new ReservationWaitingTicket(newReservation));
        return ReservationResponseDto.of(newReservation);
    }

    public void deleteReservationWaiting(Long id, LoginInfo loginInfo) {
        Reservation reservationWaiting = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("예약", id));

        if (!reservationWaiting.getMember().isSameIdWith(loginInfo.id())) {
            throw new AccessDeniedException(loginInfo.id());
        }

        reservationRepository.delete(reservationWaiting);
    }

    private static void validateReservationWaitingAvailable(List<Reservation> alreadyBookedReservations, Member member) {
        if (alreadyBookedReservations.isEmpty()) {
            throw new ReservationWaitingForbiddenException("현재 예약이 존재하지 않습니다. 예약하기 기능을 이용해주세요.");
        }
        Optional<Reservation> duplicatedReservation = alreadyBookedReservations.stream()
                .filter(reservation -> reservation.getMember().equals(member))
                .findAny();
        if (duplicatedReservation.isPresent()) {
            throw new ReservationWaitingForbiddenException("이미 예약한 이력이 있습니다.",
                    member.getId(),
                    duplicatedReservation.get().getId()
            );
        }
    }
}
