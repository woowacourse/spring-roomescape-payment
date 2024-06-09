package roomescape.service.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationDate;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InvalidMemberException;
import roomescape.exception.InvalidReservationException;
import roomescape.service.payment.PaymentService;
import roomescape.service.reservation.dto.ReservationConfirmRequest;
import roomescape.service.reservation.dto.ReservationConfirmedResponse;
import roomescape.service.reservation.dto.ReservationFilterRequest;
import roomescape.service.reservation.dto.ReservationResponse;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservationCommonService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;

    public ReservationCommonService(ReservationRepository reservationRepository, MemberRepository memberRepository, PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.paymentService = paymentService;
    }

    public List<ReservationResponse> findByCondition(ReservationFilterRequest reservationFilterRequest) {
        ReservationDate dateFrom = ReservationDate.of(reservationFilterRequest.dateFrom());
        ReservationDate dateTo = ReservationDate.of(reservationFilterRequest.dateTo());
        return reservationRepository.findBy(
                        reservationFilterRequest.memberId(),
                        reservationFilterRequest.themeId(),
                        dateFrom,
                        dateTo
                ).stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllByStatus(ReservationStatus.RESERVED).stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional
    public void deleteById(long id) {
        reservationRepository.findById(id)
                .ifPresent(reservation -> {
                    deleteIfAvailable(reservation);
                    updateIfDeletedReserved(reservation);
                });
    }

    private void deleteIfAvailable(Reservation reservation) {
        validatePastReservation(reservation);
        if (reservation.isReserved()) {
            paymentService.cancelPayment(reservation.getPayment());
        }
        reservationRepository.deleteById(reservation.getId());
    }

    private void validatePastReservation(Reservation reservation) {
        if (reservation.isReserved() && reservation.isPast()) {
            throw new InvalidReservationException("이미 지난 예약은 삭제할 수 없습니다.");
        }
    }

    private void updateIfDeletedReserved(Reservation reservation) {
        if (reservation.isReserved()) {
            ReservationDetail detail = reservation.getDetail();
            reservationRepository.findFirstByDetailIdOrderByCreatedAt(detail.getId())
                    .ifPresent(Reservation::pendingPayment);
        }
    }

    @Transactional
    public ReservationConfirmedResponse confirmReservation(ReservationConfirmRequest request, long memberId) {
        Reservation reservation = getReservationById(request.reservationId());
        Member member = getMemberById(memberId);
        validateAuthority(reservation, member);
        validateStatus(reservation);
        if (reservation.isPendingPayment()) {
            Payment payment = paymentService.approvePayment(request.paymentRequest());
            reservation.paid(payment);
        }
        return new ReservationConfirmedResponse(reservation);
    }

    private Reservation getReservationById(long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 결제 대기 정보입니다."));
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidMemberException("회원 정보를 찾을 수 없습니다."));
    }

    private void validateAuthority(Reservation reservation, Member member) {
        if (!reservation.isReservationOf(member)) {
            throw new ForbiddenException("본인의 예약만 결제할 수 있습니다.");
        }
    }

    private void validateStatus(Reservation reservation) {
        if (!reservation.isPendingPayment()) {
            throw new InvalidReservationException("결재 대기 상태에서만 결재 가능합니다.");
        }
    }
}
