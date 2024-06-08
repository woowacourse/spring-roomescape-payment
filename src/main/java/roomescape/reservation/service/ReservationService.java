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
import roomescape.reservation.dto.response.MyReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationsResponse;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.ThemeService;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeService reservationTimeService;
    private final MemberService memberService;
    private final ThemeService themeService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeService reservationTimeService,
            MemberService memberService,
            ThemeService themeService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeService = reservationTimeService;
        this.memberService = memberService;
        this.themeService = themeService;
    }

    @Transactional(readOnly = true)
    public ReservationsResponse findAllReservations() {
        List<ReservationResponse> response = reservationRepository.findAll()
                .stream()
                .filter(Reservation::isConfirmed)
                .map(ReservationResponse::from)
                .toList();

        return new ReservationsResponse(response);
    }

    @Transactional(readOnly = true)
    public ReservationsResponse findAllWaiting() {
        List<ReservationResponse> response = reservationRepository.findAll()
                .stream()
                .filter(Reservation::isWaiting)
                .map(ReservationResponse::from)
                .toList();

        return new ReservationsResponse(response);
    }

    public void removeReservationById(Long reservationId, Long memberId) {
        validateIsMemberAdmin(memberId);
        reservationRepository.deleteById(reservationId);
    }

    public Reservation addReservation(ReservationRequest request, Long memberId) {
        validateIsReservationExist(request.themeId(), request.timeId(), request.date());
        Reservation reservation = getReservationForSave(request, memberId, ReservationStatus.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    public ReservationResponse addWaiting(ReservationRequest request, Long memberId) {
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
            LocalDate requestDate,
            ReservationTime requestReservationTime
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

    private Reservation getReservationForSave(ReservationRequest request, Long memberId,
                                              ReservationStatus status) {
        ReservationTime time = reservationTimeService.findTimeById(request.timeId());
        Theme theme = themeService.findThemeById(request.themeId());
        Member member = memberService.findMemberById(memberId);

        validateDateAndTime(request.date(), time);
        return new Reservation(request.date(), time, theme, member, status);
    }

    @Transactional(readOnly = true)
    public ReservationsResponse findFilteredReservations(ReservationSearchRequest request) {
        validateDateForSearch(request.dateFrom(), request.dateTo());
        Specification<Reservation> spec = new ReservationSearchSpecification()
                .sameThemeId(request.themeId())
                .sameMemberId(request.memberId())
                .dateStartFrom(request.dateFrom())
                .dateEndAt(request.dateTo())
                .sameStatus(ReservationStatus.CONFIRMED)
                .build();

        List<ReservationResponse> response = reservationRepository.findAll(spec)
                .stream()
                .map(ReservationResponse::from)
                .toList();

        return new ReservationsResponse(response);
    }

    private void validateDateForSearch(LocalDate startFrom, LocalDate endAt) {
        if (startFrom == null || endAt == null) {
            return;
        }
        if (startFrom.isAfter(endAt)) {
            throw new RoomEscapeException(ErrorType.INVALID_DATE_RANGE,
                    String.format("[startFrom: %s, endAt: %s", startFrom, endAt), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(readOnly = true)
    public MyReservationsResponse findMemberReservations(Long memberId) {
        return new MyReservationsResponse(reservationRepository.findMyReservations(memberId));
    }

    public void approveWaiting(Long reservationId, Long memberId) {
        validateIsMemberAdmin(memberId);
        if (reservationRepository.isExistConfirmedReservation(reservationId)) {
            throw new RoomEscapeException(ErrorType.RESERVATION_DUPLICATED, HttpStatus.CONFLICT);
        }
        reservationRepository.updateStatusByReservationId(reservationId, ReservationStatus.CONFIRMED_PAYMENT_REQUIRED);
    }

    public void cancelWaiting(Long reservationId, Long memberId) {
        Reservation waiting = reservationRepository.findById(reservationId)
                .filter(Reservation::isWaiting)
                .filter(r -> r.isSameMember(memberId))
                .orElseThrow(() -> throwReservationNotFound(reservationId));
        reservationRepository.delete(waiting);
    }

    public void denyWaiting(Long reservationId, Long memberId) {
        validateIsMemberAdmin(memberId);
        Reservation waiting = reservationRepository.findById(reservationId)
                .filter(Reservation::isWaiting)
                .orElseThrow(() -> throwReservationNotFound(reservationId));
        reservationRepository.delete(waiting);
    }

    private void validateIsMemberAdmin(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        if (member.isAdmin()) {
            return;
        }
        throw new RoomEscapeException(ErrorType.PERMISSION_DOES_NOT_EXIST, HttpStatus.FORBIDDEN);
    }

    private RoomEscapeException throwReservationNotFound(Long reservationId) {
        return new RoomEscapeException(ErrorType.RESERVATION_NOT_FOUND,
                String.format("[reservationId: %d]", reservationId), HttpStatus.NOT_FOUND);
    }
}
