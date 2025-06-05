package roomescape.reservation.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.exception.ReservationException;
import roomescape.lock.service.LockService;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.RoomEscapeInformation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.AdminReservationRequest;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.RoomEscapeInformationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationCommandService {

    private final RoomEscapeInformationRepository roomEscapeInformationRepository;
    private final WaitingReservationRepository waitingReservationRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository timeRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;

    private final LockService lockService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponse resisterReservation(final ReservationRequest request, final LoginMember loginMember) {
        final ReservationTime reservationTime = timeRepository.findById(request.timeId())
                .orElseThrow(() -> new NotFoundException("found"));
        final Theme theme = findThemeById(request.themeId());
        final Member member = findMemberById(loginMember.id());
        return saveReservationInternal(request.date(), reservationTime, theme, member);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponse saveAdminReservation(final AdminReservationRequest request) {
        final ReservationTime reservationTime = findReservationTimeById(request.timeId());
        final Theme theme = findThemeById(request.themeId());
        final Member member = findMemberById(request.memberId());
        return saveReservationInternal(request.date(), reservationTime, theme, member);
    }

    public void approveWaitingReservation(final Long id) {
        final WaitingReservation waiting = waitingReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("예약 대기가 존재하지 않습니다."));
        if (hasReservation(waiting.getRoomEscapeInformation())) {
            throw new ReservationException("이미 해당 날짜에 예약이 존재합니다.");
        }
        waitingReservationRepository.deleteById(waiting.getId());
        reservationRepository.save(Reservation.of(waiting.getRoomEscapeInformation(), waiting.getMember()));
    }

    public void cancel(final Long id) {
        reservationRepository
                .findById(id)
                .ifPresent(reservation -> {
                    final Long infoId = reservation.getRoomEscapeInformation().getId();
                    reservationRepository.delete(reservation);
                    boolean hasWaiting = waitingReservationRepository
                            .existsByRoomEscapeInformationId(infoId);
                    if (!hasWaiting) {
                        roomEscapeInformationRepository.deleteById(infoId);
                    }
                });
    }

    private ReservationResponse saveReservationInternal(
            final LocalDate date,
            final ReservationTime reservationTime,
            final Theme theme,
            final Member member
    ) {
        lockService.acquireLock(generateLockKey(date, reservationTime, theme));

        final RoomEscapeInformation slot = findSlot(date, reservationTime, theme);
        validateAlreadyBooked(date, reservationTime, theme);
        final Reservation reservation = reservationRepository.save(Reservation.of(slot, member));
        return new ReservationResponse(reservation);
    }

    private String generateLockKey(final LocalDate date, final ReservationTime reservationTime, final Theme theme) {
        return String.format(
                "reservation_%s_%d_%d",
                date.toString(),
                reservationTime.getId(),
                theme.getId()
        );
    }

    private RoomEscapeInformation findSlot(final LocalDate date,
                                           final ReservationTime reservationTime,
                                           final Theme theme
    ) {
        return roomEscapeInformationRepository.findByDateAndTimeAndTheme(date, reservationTime, theme)
                .orElseGet(() -> {
                    final RoomEscapeInformation newSlot = RoomEscapeInformation.builder()
                            .date(date)
                            .theme(theme)
                            .time(reservationTime)
                            .build();
                    return roomEscapeInformationRepository.save(newSlot);
                });
    }

    private void validateAlreadyBooked(final LocalDate date, final ReservationTime reservationTime, final Theme theme) {
        if (isAlreadyBooked(date, reservationTime, theme)) {
            throw new ReservationException("이미 예약이 존재합니다.");
        }
    }

    private boolean isAlreadyBooked(final LocalDate date, final ReservationTime reservationTime, final Theme theme) {
        return reservationRepository
                .existsByRoomEscapeInformationDateAndRoomEscapeInformationTimeAndRoomEscapeInformationTheme(
                        date, reservationTime, theme);
    }

    private boolean hasReservation(final RoomEscapeInformation roomEscapeInformation) {
        return reservationRepository
                .existsByRoomEscapeInformationDateAndRoomEscapeInformationTimeAndRoomEscapeInformationTheme(
                        roomEscapeInformation.getDate(), roomEscapeInformation.getTime(), roomEscapeInformation.getTheme());
    }

    private Member findMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다."));
    }

    private ReservationTime findReservationTimeById(final Long timeId) {
        return timeRepository.findById(timeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약 시간입니다."));
    }

    private Theme findThemeById(final Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));
    }
}
