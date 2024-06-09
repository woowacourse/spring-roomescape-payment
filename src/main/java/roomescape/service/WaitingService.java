package roomescape.service;

import static roomescape.exception.RoomescapeErrorCode.MEMBER_NOT_FOUND;
import static roomescape.exception.RoomescapeErrorCode.RESERVATION_ALREADY_EXISTS;
import static roomescape.exception.RoomescapeErrorCode.RESERVATION_NOT_FOUND;
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
import roomescape.dto.reservation.ReservationDto;
import roomescape.dto.reservation.ReservationResponse;
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

    public ReservationResponse createReservationWaiting(final ReservationDto reservationDto) {
        final Member member = memberRepository.findById(reservationDto.memberId())
                .orElseThrow(() -> new RoomescapeException(MEMBER_NOT_FOUND));
        final ReservationTime time = reservationTimeRepository.findById(reservationDto.timeId())
                .orElseThrow(() -> new RoomescapeException(RESERVATION_NOT_FOUND));
        final Theme theme = themeRepository.findById(reservationDto.themeId())
                .orElseThrow(() -> new RoomescapeException(THEME_NOT_FOUND));

        final Reservation waiting = reservationDto.toModel(member, time, theme, ReservationStatus.WAITING);
        validateExistReservation(waiting);
        validateAlreadyReserved(waiting);
        validateDuplicatedWaiting(waiting);
        return ReservationResponse.from(reservationRepository.save(waiting));
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
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), ReservationStatus.WAITING,
                waiting.getMember());
        if (exists) {
            throw new RoomescapeException(WAITING_DUPLICATED);
        }
    }

    public List<ReservationResponse> findReservationWaitings() {
        final List<Reservation> waitings = reservationRepository.findByStatus(ReservationStatus.WAITING);
        return waitings.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public void approveReservationWaiting(final Long waitingId) {
        final Reservation waiting = reservationRepository.findById(waitingId)
                .orElseThrow(() -> new RoomescapeException(WAITING_NOT_FOUND));
        validateIsApprovable(waiting);
        waiting.toReserved();
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
}
