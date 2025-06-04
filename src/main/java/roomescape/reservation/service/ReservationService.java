package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
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
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.RoomEscapeInformationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingReservationRepository waitingReservationRepository;
    private final RoomEscapeInformationRepository roomEscapeInformationRepository;

    private final LockService lockService;

    private static Reservation generateReservationInstance(final Member member, final RoomEscapeInformation slot) {
        return Reservation.builder()
                .roomEscapeInformation(slot)
                .member(member)
                .build();
    }

    public List<ReservationResponse> findReservationsByCriteria(final ReservationSearchRequest request) {
        final List<Reservation> reservations = reservationRepository.findByCriteria(
                request.themeId(),
                request.memberId(),
                request.dateFrom(),
                request.dateTo()
        );
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<AvailableReservationTimeResponse> findAllReservationTime(final LocalDate date, final Long themeId) {
        return reservationTimeRepository.findAllAvailable(date, themeId);
    }

    public Reservation findById(final Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다."));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponse resisterReservation(final ReservationRequest request, final LoginMember loginMember) {
        final ReservationTime reservationTime = findReservationTimeById(request.timeId());
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

    private ReservationResponse saveReservationInternal(
            final LocalDate date,
            final ReservationTime reservationTime,
            final Theme theme,
            final Member member
    ) {
        lockService.acquireLock(generateLockKey(date, reservationTime, theme));

        final RoomEscapeInformation slot = generateSlot(date, reservationTime, theme);
        if (isAlreadyBooked(date, reservationTime, theme)) {
            throw new ReservationException("이미 예약이 존재합니다.");
        }
        final Reservation reservation = reservationRepository.save(generateReservationInstance(member, slot));
        return new ReservationResponse(reservation);
    }

    private RoomEscapeInformation generateSlot(final LocalDate date, final ReservationTime reservationTime,
                                               final Theme theme) {
        return roomEscapeInformationRepository.findByDateAndTimeAndTheme(
                        date, reservationTime, theme)
                .orElseGet(() -> {
                    final RoomEscapeInformation newSlot = RoomEscapeInformation.builder()
                            .date(date)
                            .theme(theme)
                            .time(reservationTime)
                            .build();
                    return roomEscapeInformationRepository.save(newSlot);
                });
    }

    private boolean isAlreadyBooked(final LocalDate date, final ReservationTime reservationTime, final Theme theme) {
        return reservationRepository
                .existsByRoomEscapeInformationDateAndRoomEscapeInformationTimeAndRoomEscapeInformationTheme(
                        date, reservationTime, theme);
    }

    private String generateLockKey(final LocalDate date, final ReservationTime reservationTime, final Theme theme) {
        return String.format(
                "reservation_%s_%d_%d",
                date.toString(),
                reservationTime.getId(),
                theme.getId()
        );
    }

    @Transactional
    public void deleteById(final Long id) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElse(null);
        if (reservation == null) {
            return;
        }
        final Long infoId = reservation.getRoomEscapeInformation().getId();
        reservationRepository.deleteById(id);

        final boolean hasBooked = reservationRepository.existsByRoomEscapeInformationId(infoId);
        final boolean hasWaiting = waitingReservationRepository.existsByRoomEscapeInformationId(infoId);
        if (!hasBooked && !hasWaiting) {
            roomEscapeInformationRepository.deleteById(infoId);
        }
    }

    public List<MyReservationResponse> findMyReservations(final LoginMember loginMember) {
        final Member member = findMemberById(loginMember.id());
        final List<MyReservationResponse> bookedReservations = reservationRepository.findByMember(member).stream()
                .map(MyReservationResponse::from)
                .toList();
        final List<MyReservationResponse> waitingReservations = waitingReservationRepository.findWaitingReservationByMember(
                        member).stream()
                .map(MyReservationResponse::from)
                .toList();
        return Stream.concat(bookedReservations.stream(), waitingReservations.stream())
                .toList();
    }

    public List<ReservationResponse> findAllWaitingReservation() {
        final List<WaitingReservation> waitingReservationReservations = waitingReservationRepository.findAll();
        return waitingReservationReservations.stream().map(ReservationResponse::new).toList();
    }

    @Transactional
    public void approveWaitingReservation(final Long id) {
        final WaitingReservation waitingReservation = waitingReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("예약 대기가 존재하지 않습니다."));
        if (hasReservation(waitingReservation.getRoomEscapeInformation())
        ) {
            throw new ReservationException("이미 해당 날짜에 예약이 존재합니다.");
        }
        waitingReservationRepository.deleteById(waitingReservation.getId());

        final Reservation reservation = generateReservationInstance(waitingReservation.getMember(),
                waitingReservation.getRoomEscapeInformation());
        reservationRepository.save(reservation);
    }

    private Member findMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다."));
    }

    private ReservationTime findReservationTimeById(final Long timeId) {
        return reservationTimeRepository.findById(timeId).orElseThrow(() -> new NotFoundException("존재하지 않는 예약 시간입니다."));
    }

    private Theme findThemeById(final Long themeId) {
        return themeRepository.findById(themeId).orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));
    }

    private boolean hasReservation(final RoomEscapeInformation roomEscapeInformation) {
        return reservationRepository
                .existsByRoomEscapeInformationDateAndRoomEscapeInformationTimeAndRoomEscapeInformationTheme(
                        roomEscapeInformation.getDate(), roomEscapeInformation.getTime(), roomEscapeInformation.getTheme());
    }
}
