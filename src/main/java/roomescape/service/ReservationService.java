package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.*;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static roomescape.exception.ExceptionType.*;

@Service
public class ReservationService {
    private static final int NOT_WAITING_INDEX = 1;

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

    public ReservationResponse saveByUser(LoginMemberRequest loginMemberRequest,
                                          ReservationWithPaymentRequest reservationWithPaymentRequest) {
        ReservationTime requestedTime = reservationTimeRepository.findById(reservationWithPaymentRequest.timeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION_TIME));
        Theme requestedTheme = themeRepository.findById(reservationWithPaymentRequest.themeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_THEME));
        Member requestedMember = memberRepository.findById(loginMemberRequest.id())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER));

        return save(reservationWithPaymentRequest.toReservation(
                requestedMember, requestedTime, requestedTheme));
    }

    public ReservationResponse saveByAdmin(AdminReservationRequest reservationRequest) {
        ReservationTime requestedTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION_TIME));
        Theme requestedTheme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_THEME));
        Member requestedMember = memberRepository.findById(reservationRequest.memberId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER));

        return save(new Reservation(
                reservationRequest.date(), requestedTime, requestedTheme, requestedMember,null, 0L));
    }

    private ReservationResponse save(Reservation beforeSaveReservation) {
        if (beforeSaveReservation.isBefore(LocalDateTime.now())) {
            throw new RoomescapeException(PAST_TIME_RESERVATION);
        }
        Reservation savedReservation = reservationRepository.save(beforeSaveReservation);
        return ReservationResponse.from(savedReservation);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationDetailResponse> findAllByMemberId(long userId) {
        List<Reservation> reservationsByMember = reservationRepository.findAllByMemberId(userId);
        return reservationsByMember.stream()
                .map(reservation -> ReservationDetailResponse.from(
                        reservation,
                        reservationRepository.calculateIndexOf(reservation)))
                .toList();
    }

    public List<ReservationResponse> searchReservation(Long themeId, Long memberId, LocalDate dateFrom,
                                                       LocalDate dateTo) {
        return findReservationsBy(themeId, memberId, dateFrom, dateTo).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private List<Reservation> findReservationsBy(Long themeId, Long memberId, LocalDate dateFrom,
                                                 LocalDate dateTo) {
        if (themeId != null && memberId != null) {
            return reservationRepository.findByThemeIdAndMemberIdAndDateBetween(themeId, memberId, dateFrom, dateTo);
        }
        if (themeId != null) {
            return reservationRepository.findByThemeIdAndDateBetween(themeId, dateFrom, dateTo);
        }
        if (memberId != null) {
            return reservationRepository.findByMemberIdAndDateBetween(memberId, dateFrom, dateTo);
        }
        return reservationRepository.findByDateBetween(dateFrom, dateTo);
    }

    public void deleteByUser(LoginMemberRequest loginMemberRequest, long reservationId) {
        Optional<Reservation> findResult = reservationRepository.findById(reservationId);
        if (findResult.isEmpty()) {
            return;
        }

        Reservation requestedReservation = findResult.get();
        if (requestedReservation.getMember().getId() != loginMemberRequest.id()) {
            throw new RoomescapeException(FORBIDDEN_DELETE);
        }
        reservationRepository.delete(requestedReservation);
    }

    public void deleteWaitingByAdmin(long id) {
        Optional<Reservation> findResult = reservationRepository.findById(id);
        if (findResult.isEmpty()) {
            return;
        }

        Reservation requestedReservation = findResult.get();
        if (isNotWaiting(requestedReservation)) {
            throw new RoomescapeException(FORBIDDEN_DELETE);
        }
        reservationRepository.deleteById(id);
    }

    private boolean isNotWaiting(Reservation requestedReservation) {
        return reservationRepository.calculateIndexOf(requestedReservation) == NOT_WAITING_INDEX;
    }

    public List<ReservationResponse> findAllRemainedWaiting() {
        return reservationRepository.findAllRemainedWaiting(LocalDateTime.now()).stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
