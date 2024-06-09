package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;
import roomescape.domain.reservationwaiting.WaitingWithRank;
import roomescape.service.dto.response.PersonalReservationResponse;
import roomescape.service.dto.response.ReservationResponse;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public ReservationQueryService(ReservationRepository reservationRepository,
                                   ReservationWaitingRepository reservationWaitingRepository,
                                   MemberRepository memberRepository,
                                   PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByConditions(Long memberId,
                                                                 Long themeId,
                                                                 LocalDate dateFrom,
                                                                 LocalDate dateTo) {
        List<Reservation> reservations = reservationRepository.findAllByConditions(memberId, themeId, dateFrom, dateTo);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PersonalReservationResponse> getMyReservations(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        List<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIs(member, ReservationStatus.ACCEPTED);
        List<WaitingWithRank> waitingWithRanks = reservationWaitingRepository.findAllWithRankByMember(member);

        return Stream.concat(toResponseStreamByReservations(reservations), toResponseStreamByWaitings(waitingWithRanks))
                .sorted(Comparator.comparing(PersonalReservationResponse::date)
                        .thenComparing(PersonalReservationResponse::time))
                .toList();
    }

    private Stream<PersonalReservationResponse> toResponseStreamByReservations(List<Reservation> reservations) {
        Map<Long, Payment> reservationPayments = paymentRepository.findAllByReservationIn(reservations)
                .stream()
                .collect(Collectors.toMap(Payment::getReservationId, Function.identity()));
        return reservations.stream()
                .map(r -> PersonalReservationResponse.from(r, reservationPayments.get(r.getId())));

    }

    private Stream<PersonalReservationResponse> toResponseStreamByWaitings(List<WaitingWithRank> waitingWithRanks) {
        return waitingWithRanks.stream()
                .map(PersonalReservationResponse::from);
    }
}
