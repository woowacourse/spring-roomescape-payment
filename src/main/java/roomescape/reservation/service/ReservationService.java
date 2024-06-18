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
import roomescape.reservation.dto.request.AdminReservationRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.WaitingRequest;
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
        Specification<Reservation> spec = new ReservationSearchSpecification().confirmed().build();
        List<ReservationResponse> response = findAllReservationByStatus(spec);

        return new ReservationsResponse(response);
    }

    @Transactional(readOnly = true)
    public ReservationsResponse findAllWaiting() {
        Specification<Reservation> spec = new ReservationSearchSpecification().waiting().build();
        List<ReservationResponse> response = findAllReservationByStatus(spec);

        return new ReservationsResponse(response);
    }

    private List<ReservationResponse> findAllReservationByStatus(Specification<Reservation> spec) {
        return reservationRepository.findAll(spec)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public void removeReservationById(Long reservationId, Long memberId) {
        validateIsMemberAdmin(memberId);
        reservationRepository.deleteById(reservationId);
    }

    public Reservation addReservation(ReservationRequest request, Long memberId) {
        validateIsReservationExist(request.themeId(), request.timeId(), request.date());
        Reservation reservation = getReservationForSave(request.timeId(), request.themeId(), request.date(), memberId,
                ReservationStatus.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    public ReservationResponse addReservationByAdmin(AdminReservationRequest request) {
        validateIsReservationExist(request.themeId(), request.timeId(), request.date());
        return addReservationWithoutPayment(request.themeId(), request.timeId(), request.date(),
                request.memberId(), ReservationStatus.CONFIRMED_PAYMENT_REQUIRED);
    }

    public ReservationResponse addWaiting(WaitingRequest request, Long memberId) {
        validateMemberAlreadyReserve(request.themeId(), request.timeId(), request.date(), memberId);
        return addReservationWithoutPayment(request.themeId(), request.timeId(), request.date(), memberId,
                ReservationStatus.WAITING);
    }

    private ReservationResponse addReservationWithoutPayment(Long themeId, Long timeId, LocalDate date, Long memberId,
                                                             ReservationStatus status) {
        Reservation reservation = getReservationForSave(timeId, themeId, date, memberId, status);
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
                .confirmed()
                .sameThemeId(themeId)
                .sameTimeId(timeId)
                .sameDate(date)
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

    private Reservation getReservationForSave(Long timeId, Long themeId, LocalDate date, Long memberId,
                                              ReservationStatus status) {
        ReservationTime time = reservationTimeService.findTimeById(timeId);
        Theme theme = themeService.findThemeById(themeId);
        Member member = memberService.findMemberById(memberId);

        validateDateAndTime(date, time);
        return new Reservation(date, time, theme, member, status);
    }

    @Transactional(readOnly = true)
    public ReservationsResponse findFilteredReservations(Long themeId, Long memberId, LocalDate dateFrom,
                                                         LocalDate dateTo) {
        validateDateForSearch(dateFrom, dateTo);
        Specification<Reservation> spec = new ReservationSearchSpecification()
                .confirmed()
                .sameThemeId(themeId)
                .sameMemberId(memberId)
                .dateStartFrom(dateFrom)
                .dateEndAt(dateTo)
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
