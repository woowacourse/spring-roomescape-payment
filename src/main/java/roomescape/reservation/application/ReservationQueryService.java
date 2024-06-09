package roomescape.reservation.application;

import org.springframework.stereotype.Service;
import roomescape.global.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.ThemeRepository;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.response.MyReservationResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationQueryService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;

    public ReservationQueryService(ReservationRepository reservationRepository,
                                   MemberRepository memberRepository,
                                   ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
    }

    public List<Reservation> findAllInBooking() {
        List<ReservationStatus> statusConditions = List.of(ReservationStatus.BOOKING);
        return reservationRepository.findAllByStatusWithDetails(statusConditions);
    }

    public List<Reservation> findAllInWaiting() {
        List<ReservationStatus> statusConditions = List.of(ReservationStatus.WAITING, ReservationStatus.PENDING_PAYMENT);
        return reservationRepository.findAllByStatusWithDetails(statusConditions);
    }

    public List<Reservation> findAllByMemberIdAndThemeIdAndDateBetween(Long memberId, Long themeId,
                                                                       LocalDate fromDate, LocalDate toDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 Id의 사용자가 없습니다."));
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("해당 Id의 테마가 없습니다."));
        return reservationRepository.findAllByMemberAndThemeAndStatusAndDateBetween(
                member, theme, ReservationStatus.BOOKING, fromDate, toDate);
    }

    public List<MyReservationResponse> findAllMyReservations(Member member) {
        List<ReservationPayment> reservations = reservationRepository.findAllByMemberAndStatusWithPayment(
                member, ReservationStatus.BOOKING);
        List<Reservation> reservationsPendingPayment = reservationRepository.findAllByMemberAndStatus(
                member, ReservationStatus.PENDING_PAYMENT);
        List<WaitingReservation> waitingReservations = reservationRepository.findWaitingReservationsByMember(member);
        return toMyReservationResponse(reservations, reservationsPendingPayment, waitingReservations);
    }

    private List<MyReservationResponse> toMyReservationResponse(List<ReservationPayment> reservationsWithPayment,
                                                                List<Reservation> reservationsPendingPayment,
                                                                List<WaitingReservation> waitingReservations) {
        List<MyReservationResponse> myReservations = new ArrayList<>();
        myReservations.addAll(reservationsWithPayment.stream().map(MyReservationResponse::from).toList());
        myReservations.addAll(reservationsPendingPayment.stream().map(MyReservationResponse::from).toList());
        myReservations.addAll(waitingReservations.stream().map(MyReservationResponse::from).toList());
        return myReservations;
    }
}
