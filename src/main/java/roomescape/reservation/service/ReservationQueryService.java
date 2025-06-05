package roomescape.reservation.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final WaitingReservationRepository waitingReservationRepository;

    public Reservation findById(final Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다."));
    }

    public List<ReservationResponse> findAllWaitingReservation() {
        final List<WaitingReservation> waitingReservationReservations = waitingReservationRepository.findAll();
        return waitingReservationReservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<MyReservationResponse> findMyReservations(final LoginMember loginMember) {
        final Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다."));
        final List<MyReservationResponse> bookedReservations = findBookedReservations(member);
        final List<MyReservationResponse> waitingReservations = findWaitingReservations(member);
        return Stream.concat(bookedReservations.stream(), waitingReservations.stream())
                .toList();
    }

    public List<ReservationResponse> findReservationsByCriteria(final ReservationSearchRequest request) {
        final List<Reservation> reservations = reservationRepository.findByCriteria(
                request.themeId(),
                request.memberId(),
                request.dateFrom(),
                request.dateTo()
        );
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    private List<MyReservationResponse> findBookedReservations(final Member member) {
        final List<Payment> payments = paymentRepository.findPaymentsByMember(member);
        final Map<Long, Payment> paymentByReservationId = payments.stream()
                .collect(Collectors.toMap(
                        p -> p.getReservation().getId(),
                        Function.identity()
                ));
        return reservationRepository.findByMember(member).stream()
                .map(reservation -> {
                    final Payment payment = paymentByReservationId.get(reservation.getId());
                    return MyReservationResponse.from(reservation, payment);
                })
                .toList();
    }

    private List<MyReservationResponse> findWaitingReservations(final Member member) {
        return waitingReservationRepository.findWaitingReservationByMember(member)
                .stream()
                .map(MyReservationResponse::from)
                .toList();
    }
}
