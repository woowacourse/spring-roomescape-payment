package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.*;
import roomescape.domain.repository.*;
import roomescape.dto.request.ReservationCondition;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWithStatusResponse;
import roomescape.exception.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              MemberRepository memberRepository,
                              WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservations(ReservationCondition cond) {
        List<Reservation> filteredReservations = reservationRepository.findByCondition(cond);
        return filteredReservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationWithStatusResponse> findBookingHistory(Long memberId) {
        List<ReservationWithStatusResponse> reservations = findReservationByMemberId(memberId);
        List<ReservationWithStatusResponse> waitings = findWaitingByMemberId(memberId);
        return Stream.concat(reservations.stream(), waitings.stream())
                .toList();
    }

    private List<ReservationWithStatusResponse> findReservationByMemberId(Long memberId) {
        return reservationRepository.findByMemberId(memberId).stream()
                .map(ReservationWithStatusResponse::from)
                .toList();
    }

    private List<ReservationWithStatusResponse> findWaitingByMemberId(Long memberId) {
        return waitingRepository.findByMemberIdSortedByCreateAt(memberId)
                .stream()
                .map(ReservationWithStatusResponse::from)
                .toList();
    }

    public ReservationResponse createReservation(Long memberId, Long timeId, Long themeId, LocalDate date) {

        ReservationTime reservationTime = reservationTimeRepository.findById(timeId)
                .orElseThrow(ReservationTimeNotFoundException::new);
        Theme theme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Reservation reservation = Reservation.createWithoutId(member, date, reservationTime, theme);

        reservation.validateDateTime();
        validateDuplicate(date, reservationTime, theme);

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    public ReservationResponse createReservationForMember(Long memberId,
                                                          Long timeId,
                                                          Long themeId,
                                                          LocalDate date) {

        ReservationTime reservationTime = reservationTimeRepository.findById(timeId)
                .orElseThrow(ReservationTimeNotFoundException::new);
        Theme theme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Reservation reservation = Reservation.createWithoutId(member, date, reservationTime, theme);

        reservation.validateDateTime();
        validateDuplicate(date, reservationTime, theme);

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }


    private void validateDuplicate(LocalDate date, ReservationTime time, Theme theme) {
        if (reservationRepository.findByDateAndReservationTimeAndTheme(date, time, theme).isPresent()) {
            throw new ExistedReservationException();
        }
    }

    public void deleteReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(ReservationNotFoundException::new);
        reservationRepository.deleteById(id);

        promoteWaitingToReservationIfExist(reservation);
    }

    private void promoteWaitingToReservationIfExist(Reservation reservation) {
        List<WaitingWithRank> waitings = waitingRepository.findByDateAndReservationTimeAndThemeSortedByCreateAt(
                reservation.getDate(),
                reservation.getReservationTime().getId(),
                reservation.getTheme().getId());

        if (!waitings.isEmpty()) {
            Waiting firstWaiting = waitings.getFirst().waiting();
            waitingRepository.deleteById(firstWaiting.getId());
            Reservation promotedReservation = firstWaiting.promoteToReservation();
            reservationRepository.save(promotedReservation);
        }
    }
}
