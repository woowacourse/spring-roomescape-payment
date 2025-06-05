package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.*;
import roomescape.domain.repository.*;
import roomescape.dto.PaymentRequest;
import roomescape.dto.request.ReservationCondition;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.response.MyReservationsResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWithPaymentResponse;
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
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public ReservationService(ReservationRepository reservationRepository, ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository, MemberRepository memberRepository, WaitingRepository waitingRepository,
                              PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservations(ReservationCondition cond) {
        List<Reservation> filteredReservations = reservationRepository.findByCondition(cond);
        return filteredReservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyReservationsResponse> findBookingHistory(Long memberId) {
        List<MyReservationsResponse> reservations = findReservationByMemberId(memberId);
        List<MyReservationsResponse> waitings = findWaitingByMemberId(memberId);
        return Stream.concat(reservations.stream(), waitings.stream())
                .toList();
    }

    private List<MyReservationsResponse> findReservationByMemberId(Long memberId) {
        return reservationRepository.findByMemberId(memberId).stream()
                .map(reservation -> {
                            Payment payment = paymentRepository.findByReservation(reservation).orElseThrow(PaymentNotFoundException::new);
                            return MyReservationsResponse.of(reservation, payment);
                        }
                )
                .toList();
    }

    private List<MyReservationsResponse> findWaitingByMemberId(Long memberId) {
        return waitingRepository.findByMemberIdSortedByCreateAt(memberId)
                .stream()
                .map(MyReservationsResponse::from)
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

    public ReservationWithPaymentResponse createReservationForMember(Long memberId, ReservationCreateRequest request) {
        Reservation reservation = saveReservation(request, memberId);
        Payment savedPayment = processPayment(request, reservation);
        return ReservationWithPaymentResponse.of(reservation, savedPayment);
    }

    private Reservation saveReservation(ReservationCreateRequest request, Long memberId) {
        ReservationTime reservationTime = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(ReservationTimeNotFoundException::new);
        Theme theme = themeRepository.findById(request.themeId()).orElseThrow(ThemeNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Reservation reservation = Reservation.createWithoutId(member, request.date(), reservationTime, theme);
        validateReservation(request, reservation, reservationTime, theme);
        return reservationRepository.save(reservation);
    }

    private void validateReservation(ReservationCreateRequest request, Reservation reservation, ReservationTime reservationTime, Theme theme) {
        reservation.validateDateTime();
        validateDuplicate(request.date(), reservationTime, theme);
    }

    private Payment processPayment(ReservationCreateRequest request, Reservation reservation) {
        PaymentRequest paymentRequest = new PaymentRequest(request.amount(), request.paymentKey(), request.orderId());
        PaymentInfo paymentInfo = paymentClient.postPaymentInfo(paymentRequest);

        Payment payment = new Payment(paymentInfo, reservation);
        return paymentRepository.save(payment);
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
