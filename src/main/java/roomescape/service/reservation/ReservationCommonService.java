package roomescape.service.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationDate;
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
    private final PaymentService paymentService;

    public ReservationCommonService(ReservationRepository reservationRepository, PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
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
        Reservation reservation = getById(request.reservationId());
        if (reservation.isPendingPayment()) {
            Payment payment = paymentService.approvePayment(request.paymentRequest());
            reservation.paid(payment);
        }
        return new ReservationConfirmedResponse(reservation);
    }

    private Reservation getById(long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 결제 대기 정보입니다."));
    }
}
