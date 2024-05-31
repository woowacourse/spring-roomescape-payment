package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.*;
import roomescape.domain.reservation.dto.ReservationReadOnly;
import roomescape.domain.reservation.slot.ReservationSlot;
import roomescape.exception.AuthorizationException;
import roomescape.exception.RoomEscapeBusinessException;
import roomescape.infrastructure.PaymentClient;
import roomescape.service.dto.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class ReservationService {

    private final ReservationSlotService reservationSlotService;
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final PaymentClient paymentClient;

    public ReservationService(
            ReservationSlotService reservationSlotService,
            ReservationRepository reservationRepository,
            WaitingRepository waitingRepository,
            MemberRepository memberRepository,
            PaymentClient paymentClient
    ) {
        this.reservationSlotService = reservationSlotService;
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public ReservationResponse saveReservation(ReservationRequest reservationRequest) {
        Runnable payment = () -> {};
        return saveReservation(reservationRequest, payment);
    }

    @Transactional
    public ReservationResponse saveReservation(ReservationPaymentRequest reservationPaymentRequest) {
        ReservationRequest reservationRequest = reservationPaymentRequest.toReservationRequest();
        PaymentRequest paymentRequest = reservationPaymentRequest.toPaymentRequest();
        Runnable payment = () -> paymentClient.confirm(paymentRequest);
        return saveReservation(reservationRequest, payment);
    }

    private ReservationResponse saveReservation(ReservationRequest reservationRequest, Runnable payment) {
        Member member = findMemberById(reservationRequest.memberId());
        ReservationSlot slot = reservationSlotService.findSlot(reservationRequest.toSlotRequest());
        return trySave(member, slot, () -> {
            payment.run();
            Reservation savedReservation = reservationRepository.save(new Reservation(member, slot));
            return ReservationResponse.createByReservation(savedReservation);
        });
    }

    @Transactional
    public ReservationResponse saveWaiting(WaitingSaveRequest waitingSaveRequest) {
        Member member = findMemberById(waitingSaveRequest.memberId());
        ReservationSlot slot = reservationSlotService.findSlot(waitingSaveRequest.toSlotRequest());
        return trySave(member, slot, () -> {
            throw new RoomEscapeBusinessException("해당 대기에 대한 예약이 존재하지 않습니다.");
        }); // TODO: WaitingResponse 분리? => private 메서드 추출 못 함
    }

    private ReservationResponse trySave(Member member,
                                        ReservationSlot slot,
                                        Supplier<ReservationResponse> progressWhenReservationNotExist) {
        Optional<Reservation> optionalReservation = reservationRepository.findBySlot(slot);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            Waiting waiting = reservation.addWaiting(member);
            waitingRepository.save(waiting);
            return ReservationResponse.createByWaiting(waiting);
        }
        return progressWhenReservationNotExist.get();
    }

    @Transactional(readOnly = true)
    public List<ReservationBookedResponse> findReservationsByCondition(
            ReservationConditionRequest reservationConditionRequest) {
        List<ReservationReadOnly> reservations = reservationRepository.findByConditions(
                reservationConditionRequest.dateFrom(),
                reservationConditionRequest.dateTo(),
                reservationConditionRequest.themeId(),
                reservationConditionRequest.memberId()
        );

        return reservations.stream()
                .map(ReservationBookedResponse::from)
                .sorted(Comparator.comparing(ReservationBookedResponse::dateTime))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WaitingResponse> findAllWaiting() {
        return waitingRepository.findAllReadOnly().stream()
                .map(WaitingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserReservationResponse> findMyAllReservationAndWaiting(Long memberId, LocalDate date) {
        Member member = findMemberById(memberId);
        List<Reservation> reservations = reservationRepository.findByMemberAndSlot_DateGreaterThanEqual(member, date);

        List<WaitingRank> waitingRanks = waitingRepository.findRankByMemberAndDateGreaterThanEqual(member, date);

        return Stream.concat(
                        UserReservationResponse.reservationsToResponseStream(reservations),
                        UserReservationResponse.waitingsToResponseStream(waitingRanks)
                )
                .sorted(Comparator.comparing(UserReservationResponse::dateTime))
                .toList();
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeBusinessException("존재하지 않는 예약입니다."));

        if (reservation.hasNotWaiting()) {
            reservationRepository.delete(reservation);
            return;
        }

        reservation.approveWaiting();
    }

    @Transactional
    public void cancelWaiting(Long id, LoginMember loginMember) {
        Waiting foundWaiting = waitingRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeBusinessException("존재하지 않는 예약 대기입니다."));

        if (loginMember.isUser() && foundWaiting.isNotMemberId(loginMember.id())) {
            throw new AuthorizationException();
        }

        waitingRepository.delete(foundWaiting);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomEscapeBusinessException("회원이 존재하지 않습니다."));
    }
}

