package roomescape.service;

import static roomescape.exception.ExceptionType.DUPLICATE_RESERVATION;
import static roomescape.exception.ExceptionType.DUPLICATE_WAITING_RESERVATION;
import static roomescape.exception.ExceptionType.NOT_FOUND_MEMBER_BY_ID;
import static roomescape.exception.ExceptionType.NOT_FOUND_RESERVATION_TIME;
import static roomescape.exception.ExceptionType.NOT_FOUND_THEME;
import static roomescape.exception.ExceptionType.PAST_TIME_RESERVATION;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.domain.LoginMember;
import roomescape.domain.ReservationStatus;
import roomescape.domain.Waiting;
import roomescape.dto.AdminReservationDetailResponse;
import roomescape.dto.AdminReservationRequest;
import roomescape.dto.ReservationDetailResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.entity.Member;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public ReservationResponse save(LoginMember loginMember, ReservationRequest request) {
        validateDuplicatedReservation(request);

        Reservation reservation = getReservation(loginMember.getId(), request, ReservationStatus.BOOKED);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    public ReservationResponse saveByAdmin(AdminReservationRequest adminReservationRequest) {
        ReservationRequest request = ReservationRequest.from(adminReservationRequest);
        validateDuplicatedReservation(request);

        Reservation beforeSaveReservation = getReservation(adminReservationRequest.memberId(), request, ReservationStatus.BOOKED);
        return ReservationResponse.from(reservationRepository.save(beforeSaveReservation));
    }

    private void validateDuplicatedReservation(ReservationRequest request) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId())) {
            throw new RoomescapeException(DUPLICATE_RESERVATION, request.date(), request.themeId(), request.timeId());
        }
    }

    public ReservationResponse saveWaiting(LoginMember loginMember, ReservationRequest request) {
        if (reservationRepository.existsByMemberIdAndDateAndTimeIdAndThemeId(loginMember.getId(), request.date(), request.timeId(), request.themeId())) {
            throw new RoomescapeException(DUPLICATE_WAITING_RESERVATION, loginMember.getId(), request.date(), request.themeId(), request.timeId());
        }

        Reservation reservation = getReservation(loginMember.getId(), request, ReservationStatus.WAITING);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }


    private Reservation getReservation(long memberId, ReservationRequest reservationRequest, ReservationStatus reservationStatus) {
        ReservationTime requestedTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION_TIME, reservationRequest.timeId()));
        Theme requestedTheme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_THEME, reservationRequest.themeId()));
        Member requestedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER_BY_ID, memberId));

        Reservation reservation = reservationRequest.toReservation(
                requestedMember,
                requestedTime,
                requestedTheme,
                reservationStatus);

        if (reservation.isBefore(LocalDateTime.now())) {
            throw new RoomescapeException(PAST_TIME_RESERVATION, reservation.getReservationTime().getStartAt());
        }
        return reservation;
    }

    public List<ReservationResponse> findAllReservations() {
        List<Reservation> reservations = reservationRepository.findAllByStatus(ReservationStatus.BOOKED);
        return toReservationResponses(reservations);
    }

    public List<ReservationResponse> searchReservation(Long themeId, Long memberId, LocalDate dateFrom, LocalDate dateTo) {
        List<Reservation> reservations = reservationRepository.findByThemeIdAndMemberIdAndStatusAndDateBetween(themeId, memberId, ReservationStatus.BOOKED, dateFrom, dateTo);
        return toReservationResponses(reservations);
    }

    private List<ReservationResponse> toReservationResponses(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteById(long reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    @Transactional
    public void deleteByMemberIdAndId(LoginMember loginMember, long id) {
        reservationRepository.deleteByMemberIdAndId(loginMember.getId(), id);
    }

    @Transactional
    public List<ReservationDetailResponse> findAllByMemberId(long memberId) {
        List<Reservation> waitingReservations = reservationRepository.findAllByMemberIdAndStatus(memberId, ReservationStatus.WAITING);
        List<Waiting> waitings = getWaitings(waitingReservations);

        List<Reservation> bookedReservations = reservationRepository.findAllByMemberIdAndStatus(memberId, ReservationStatus.BOOKED);
        return ReservationDetailResponse.of(bookedReservations, waitings);
    }

    @Transactional
    public List<AdminReservationDetailResponse> findAllWaitingReservations() {
        List<Reservation> reservations = reservationRepository.findAllByStatus(ReservationStatus.WAITING);
        return getWaitings(reservations).stream()
                .map(AdminReservationDetailResponse::from)
                .toList();
    }

    private List<Waiting> getWaitings(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> Waiting.of(reservation, findWaitingRanking(reservation)))
                .filter(Waiting::remainsWaitingRank)
                .toList();
    }

    private long findWaitingRanking(Reservation reservation) {
        return reservationRepository.findAndCountWaitingNumber(
                reservation.getDate(),
                reservation.getReservationTime(),
                reservation.getTheme(),
                reservation.getCreatedAt());
    }
}
