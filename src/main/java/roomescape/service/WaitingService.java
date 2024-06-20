package roomescape.service;

import static roomescape.exception.RoomescapeErrorCode.MEMBER_NOT_FOUND;
import static roomescape.exception.RoomescapeErrorCode.RESERVATION_ALREADY_EXISTS;
import static roomescape.exception.RoomescapeErrorCode.RESERVATION_TIME_NOT_FOUND;
import static roomescape.exception.RoomescapeErrorCode.THEME_NOT_FOUND;
import static roomescape.exception.RoomescapeErrorCode.WAITING_DUPLICATED;
import static roomescape.exception.RoomescapeErrorCode.WAITING_FOR_MY_RESERVATION;
import static roomescape.exception.RoomescapeErrorCode.WAITING_FOR_NO_RESERVATION;
import static roomescape.exception.RoomescapeErrorCode.WAITING_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationWaitingRequest;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@Transactional
@Service
public class WaitingService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public WaitingService(
            final ReservationRepository reservationRepository,
            final MemberRepository memberRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final ThemeRepository themeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    public ReservationResponse createReservationWaiting(final ReservationWaitingRequest request) {
        final Reservation waiting = Reservation.builder()
                .member(getMemberById(request.memberId()))
                .date(request.date())
                .time(getTimeById(request.timeId()))
                .theme(getThemeById(request.themeId()))
                .status(ReservationStatus.PENDING)
                .build();
        waiting.validateDateTime();
        validateExistReservation(waiting);
        validateAlreadyReserved(waiting);
        validateDuplicatedWaiting(waiting);
        return new ReservationResponse(reservationRepository.save(waiting));
    }

    private void validateExistReservation(final Reservation waiting) {
        final boolean exists = reservationRepository.existsByThemeAndDateAndTimeAndStatus(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED);
        if (!exists) {
            throw new RoomescapeException(WAITING_FOR_NO_RESERVATION);
        }
    }

    private void validateAlreadyReserved(final Reservation waiting) {
        final boolean exists = reservationRepository.existsByThemeAndDateAndTimeAndStatusAndMember(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED,
                waiting.getMember());
        if (exists) {
            throw new RoomescapeException(WAITING_FOR_MY_RESERVATION);
        }
    }

    private void validateDuplicatedWaiting(final Reservation waiting) {
        final boolean exists = reservationRepository.existsByThemeAndDateAndTimeAndStatusAndMember(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.PENDING,
                waiting.getMember());
        if (exists) {
            throw new RoomescapeException(WAITING_DUPLICATED);
        }
    }

    public List<ReservationResponse> findReservationWaitings() {
        final List<Reservation> waitings = reservationRepository.findByStatus(ReservationStatus.PENDING);
        return waitings.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public void approveReservationWaiting(final Long waitingId) {
        final Reservation waiting = reservationRepository.findById(waitingId)
                .orElseThrow(() -> new RoomescapeException(WAITING_NOT_FOUND));
        validateIsApprovable(waiting);
        waiting.reserve();
    }

    private void validateIsApprovable(final Reservation waiting) {
        final boolean exists = reservationRepository.existsByThemeAndDateAndTimeAndStatus(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.RESERVED);
        if (exists) {
            throw new RoomescapeException(RESERVATION_ALREADY_EXISTS);
        }
    }

    public void rejectReservationWaiting(final Long id) {
        final boolean isExist = reservationRepository.existsById(id);
        if (!isExist) {
            throw new RoomescapeException(WAITING_NOT_FOUND);
        }
        reservationRepository.deleteById(id);
    }

    public Member getMemberById(final Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new RoomescapeException(MEMBER_NOT_FOUND));
    }

    private ReservationTime getTimeById(final Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(RESERVATION_TIME_NOT_FOUND));
    }

    private Theme getThemeById(final Long id) {
        return themeRepository.findById(id).orElseThrow(() -> new RoomescapeException(THEME_NOT_FOUND));
    }
}
