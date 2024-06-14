package roomescape.reservation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.member.model.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.SaveReservationRequest;
import roomescape.reservation.dto.SearchReservationsRequest;
import roomescape.reservation.model.*;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;
import roomescape.reservation.repository.WaitingRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentService paymentService;

    public ReservationService(
            final ReservationRepository reservationRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final ThemeRepository themeRepository,
            final MemberRepository memberRepository,
            final WaitingRepository waitingRepository,
            final PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentService = paymentService;
    }

    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> searchReservations(final SearchReservationsRequest request) {
        return reservationRepository.searchReservations(
                request.memberId(),
                request.themeId(),
                new ReservationDate(request.from()),
                new ReservationDate(request.to())
        );
    }

    public Reservation saveReservation(final SaveReservationRequest request) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 예약 시간이 존재하지 않습니다."));
        final Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 테마가 존재하지 않습니다."));
        final Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 회원이 존재하지 않습니다."));

        final Reservation reservation = request.toReservation(reservationTime, theme, member);
        validateReservationDateAndTime(reservation.getDate(), reservationTime);
        validateReservationDuplication(reservation);

        Payment payment = paymentService.requestTossPayment(request.toPaymentRequest());
        reservation.updatePayment(payment);
        return reservationRepository.save(reservation);
    }

    private static void validateReservationDateAndTime(final ReservationDate date, final ReservationTime time) {
        final LocalDateTime reservationLocalDateTime = LocalDateTime.of(date.getValue(), time.getStartAt());
        if (reservationLocalDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("현재 날짜보다 이전 날짜를 예약할 수 없습니다.");
        }
    }

    private void validateReservationDuplication(final Reservation reservation) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId())
        ) {
            throw new IllegalArgumentException("이미 해당 날짜/시간의 테마 예약이 있습니다.");
        }
    }

    @Transactional
    public void deleteReservation(final Long reservationId) {
        if (!waitingRepository.existsByReservationId(reservationId)) {
            reservationRepository.deleteById(reservationId);
            return;
        }
        //기존 예약의 멤버를 대기 1번 멤버로 바꿔줘야함.
        Reservation reservation = reservationRepository.findById(reservationId).get();
        Member firstCandidate = waitingRepository.findFirstMemberByReservationIdOrderByIdAsc(reservationId);
        reservation.updateMember(firstCandidate);
        waitingRepository.deleteByMemberAndReservation(firstCandidate, reservation);
    }

    public List<MyReservationResponse> getMyReservations(final Long memberId) {
        List<WaitingWithRank> waitingsWithRank = waitingRepository.findWaitingsWithRank(memberId);
        List<MyReservationResponse> myWaitings = waitingsWithRank.stream()
                .map(MyReservationResponse::new)
                .toList();

        List<MyReservationResponse> myReservationResponses = reservationRepository.findAllByMemberIdWithPayment(memberId);
        List<MyReservationResponse> myReservations = new ArrayList<>(myReservationResponses);

        myReservations.addAll(myWaitings);
        return myReservations;
    }

}
