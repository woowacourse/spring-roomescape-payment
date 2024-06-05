package roomescape.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSearchSpecification;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.ReservationSearchRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationsResponse;
import roomescape.reservation.dto.response.WaitingWithRankResponse;
import roomescape.reservation.dto.response.WaitingWithRanksResponse;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.ThemeService;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeService reservationTimeService;
    private final MemberService memberService;
    private final ThemeService themeService;

    public ReservationService(
            final ReservationRepository reservationRepository,
            final ReservationTimeService reservationTimeService,
            final MemberService memberService,
            final ThemeService themeService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeService = reservationTimeService;
        this.memberService = memberService;
        this.themeService = themeService;
    }

    public ReservationsResponse findAllReservations() {
        final List<ReservationResponse> response = reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();

        return new ReservationsResponse(response);
    }

    @Transactional
    public void removeReservationById(final Long reservationId, final Long memberId) {
        validateIsMemberAdmin(memberId);
        reservationRepository.deleteById(reservationId);
    }

    public ReservationResponse addReservation(final ReservationRequest request, final Long memberId) {
        validateIsReservationExist(request.themeId(), request.timeId(), request.date());
        Reservation reservation = getReservationForSave(request, memberId, ReservationStatus.CONFIRMED);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    public ReservationResponse addWaiting(final ReservationRequest request, final Long memberId) {
        validateMemberAlreadyReserve(request.themeId(), request.timeId(), request.date(), memberId);
        Reservation reservation = getReservationForSave(request, memberId, ReservationStatus.WAITING);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    private void validateMemberAlreadyReserve(Long themeId, Long timeId, LocalDate date, Long memberId) {
        Specification<Reservation> spec = new ReservationSearchSpecification()
                .sameMemberId(memberId)
                .sameThemeId(themeId)
                .sameTimeId(timeId)
                .sameDate(date)
                .build();

        if (reservationRepository.exists(spec)) {
            throw new RoomEscapeException(ErrorType.HAS_RESERVATION_OR_WAITING, HttpStatus.BAD_REQUEST);
        }
    }

    private void validateIsReservationExist(Long themeId, Long timeId, LocalDate date) {
        Specification<Reservation> spec = new ReservationSearchSpecification()
                .sameThemeId(themeId)
                .sameTimeId(timeId)
                .sameDate(date)
                .sameStatus(ReservationStatus.CONFIRMED)
                .build();

        if (reservationRepository.exists(spec)) {
            throw new RoomEscapeException(ErrorType.RESERVATION_DUPLICATED, HttpStatus.CONFLICT);
        }
    }

    private void validateDateAndTime(
            final LocalDate requestDate,
            final ReservationTime requestReservationTime
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime request = LocalDateTime.of(requestDate, requestReservationTime.getStartAt());
        if (request.isBefore(now)) {
            throw new RoomEscapeException(ErrorType.RESERVATION_PERIOD_IN_PAST,
                    String.format("[now: %s %s | request: %s %s]",
                            now.toLocalDate(), now.toLocalTime(), requestDate, requestReservationTime.getStartAt()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private Reservation getReservationForSave(final ReservationRequest request, final Long memberId,
                                              final ReservationStatus status) {
        final ReservationTime time = reservationTimeService.findTimeById(request.timeId());
        final Theme theme = themeService.findThemeById(request.themeId());
        final Member member = memberService.findMemberById(memberId);

        validateDateAndTime(request.date(), time);
        return new Reservation(request.date(), time, theme, member, status);
    }

    public ReservationsResponse findFilteredReservations(final ReservationSearchRequest request) {
        validateDateForSearch(request.dateFrom(), request.dateTo());
        final Specification<Reservation> spec = new ReservationSearchSpecification()
                .sameThemeId(request.themeId())
                .sameMemberId(request.memberId())
                .dateStartFrom(request.dateFrom())
                .dateEndAt(request.dateTo())
                .sameStatus(ReservationStatus.CONFIRMED)
                .build();

        final List<ReservationResponse> response = reservationRepository.findAll(spec)
                .stream()
                .map(ReservationResponse::from)
                .toList();

        return new ReservationsResponse(response);
    }

    private void validateDateForSearch(final LocalDate startFrom, final LocalDate endAt) {
        if (startFrom == null || endAt == null) {
            return;
        }
        if (startFrom.isAfter(endAt)) {
            throw new RoomEscapeException(ErrorType.INVALID_DATE_RANGE,
                    String.format("[startFrom: %s, endAt: %s", startFrom, endAt), HttpStatus.BAD_REQUEST);
        }
    }

    public WaitingWithRanksResponse findWaitingWithRankById(final Long memberId) {
        final List<WaitingWithRankResponse> waitingWithRanks = reservationRepository.findWaitingsWithRankByMemberId(
                        memberId)
                .stream()
                .map(WaitingWithRankResponse::from)
                .toList();
        return new WaitingWithRanksResponse(waitingWithRanks);
    }

    public void approveWaiting(final Long reservationId, final Long memberId) {
        validateIsMemberAdmin(memberId);
        if (reservationRepository.isExistConfirmedReservation(reservationId)) {
            throw new RoomEscapeException(ErrorType.RESERVATION_DUPLICATED, HttpStatus.CONFLICT);
        }
        reservationRepository.updateStatusByReservationId(reservationId, ReservationStatus.CONFIRMED);
    }

    public void cancelWaiting(final Long reservationId) {
        Reservation waiting = reservationRepository.findById(reservationId)
                .filter(r -> r.getReservationStatus() == ReservationStatus.WAITING)
                .orElseThrow(() -> new RoomEscapeException(ErrorType.RESERVATION_NOT_FOUND,
                        String.format("[reservationId: %d]", reservationId), HttpStatus.BAD_REQUEST));
        reservationRepository.delete(waiting);
    }

    public void denyWaiting(Long reservationId, Long memberId) {
        validateIsMemberAdmin(memberId);
        cancelWaiting(reservationId);
    }

    private void validateIsMemberAdmin(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        if (member.isAdmin()) {
            return;
        }
        throw new RoomEscapeException(ErrorType.PERMISSION_DOES_NOT_EXIST, HttpStatus.FORBIDDEN);
    }
}
