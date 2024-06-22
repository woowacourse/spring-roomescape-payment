package roomescape.reservation.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.custom.BadRequestException;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.specification.ReservationSpecification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationRegister {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationRegister(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservations(ReservationQueryRequest request) {
        Specification<Reservation> spec = Specification
                .where(ReservationSpecification.greaterThanOrEqualToStartDate(request.getStartDate()))
                .and(ReservationSpecification.lessThanOrEqualToEndDate(request.getEndDate()))
                .and(ReservationSpecification.equalMemberId(request.getMemberId()))
                .and(ReservationSpecification.equalThemeId(request.getThemeId()));
        return reservationService.findReservations(spec)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationWithStatus> findReservations(AuthInfo authInfo) {
        return reservationService.findReservationsByMemberId(authInfo.getId())
                .stream()
                .map(ReservationWithStatus::from)
                .toList();
    }

    public ReservationResponse reserve(ReservationPaymentRequest reservationPaymentRequest, Long memberId) {
        ReservationRequest reservationRequest = new ReservationRequest(reservationPaymentRequest.date(), reservationPaymentRequest.timeId(), reservationPaymentRequest.themeId());
        if (reservationService.hasSameReservation(
                LocalDate.parse(reservationRequest.date()),
                reservationRequest.themeId(),
                reservationRequest.timeId())
        ) {
            throw new BadRequestException("이미 예약된 상태입니다. 예약 대기를 진행하거나 다른 예약을 선택해주세요.");
        }
        PaymentRequest paymentRequest = new PaymentRequest(reservationPaymentRequest);
        Payment payment = paymentService.purchase(paymentRequest, reservationPaymentRequest.amount());
        return createReservationWithPayment(reservationRequest, memberId, ReservationStatus.BOOKED, payment);
    }

    public ReservationResponse confirmReservation(WaitingReservationPaymentRequest waitingReservationPaymentRequest, Long memberId) {
        PaymentRequest paymentRequest = new PaymentRequest(waitingReservationPaymentRequest);
        Payment payment = paymentService.purchase(paymentRequest, waitingReservationPaymentRequest.amount());
        Reservation reservation = reservationService.payReservation(
                LocalDate.parse(waitingReservationPaymentRequest.date()),
                waitingReservationPaymentRequest.theme(),
                LocalTime.parse(waitingReservationPaymentRequest.time()),
                memberId,
                payment);
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse createReservationWithPayment(
            ReservationRequest reservationRequest, Long memberId, ReservationStatus reservationStatus, Payment payment
    ) {
        Reservation reservation = reservationService.createReservationWithPayment(reservationRequest, memberId, reservationStatus, payment);
        return ReservationResponse.from(reservation.getId(), reservation.getReservationSlot(), reservation.getMember());
    }

    public ReservationResponse createReservation(
            ReservationRequest reservationRequest, Long memberId, ReservationStatus reservationStatus
    ) {
        Reservation reservation = reservationService.createReservation(reservationRequest, memberId, reservationStatus);
        return ReservationResponse.from(reservation.getId(), reservation.getReservationSlot(), reservation.getMember());
    }

    public void deleteReservation(AuthInfo authInfo, long reservationId) {
        reservationService.deleteReservation(authInfo, reservationId);
    }

    public void deleteReservationSlot(long reservationSlotId) {
        reservationService.deleteReservationSlot(reservationSlotId);
    }
}
