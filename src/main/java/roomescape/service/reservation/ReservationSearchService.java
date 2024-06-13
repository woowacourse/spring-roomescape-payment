package roomescape.service.reservation;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.dto.reservation.ReservationFilter;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.UserReservationResponse;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
@Transactional(readOnly = true)
public class ReservationSearchService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public ReservationSearchService(ReservationRepository reservationRepository, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    public ReservationResponse findReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);
        return ReservationResponse.from(reservation);
    }

    public List<ReservationResponse> findAllReservedReservations() {
        return reservationRepository.findByStatusEquals(Status.RESERVED)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findReservationsByFilter(ReservationFilter filter) {
        List<Reservation> reservations = reservationRepository.findByMemberOrThemeOrDateRangeAndStatus(
                filter.member(),
                filter.theme(),
                filter.dateFrom(),
                filter.dateTo(),
                Status.RESERVED
        );

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<UserReservationResponse> findReservationByMemberId(Long memberId) {
        return reservationRepository.findByMemberId(memberId)
                .stream()
                .map(this::createUserReservationResponse)
                .toList();
    }

    public List<ReservationResponse> findAllWaitingReservations() {
        return reservationRepository.findByStatusEquals(Status.WAITING)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private UserReservationResponse createUserReservationResponse(Reservation reservation) {
        if (reservation.isReserved() && paymentRepository.existsByReservationId(reservation.getId())) {
            Payment payment = paymentRepository.findByReservationIdOrThrow(reservation.getId());
            return UserReservationResponse.createByContainPayment(reservation, payment);
        }

        if (reservation.isReserved() || reservation.isPaymentPending()) {
            return UserReservationResponse.create(reservation);
        }

        int reservationOrder = reservationRepository.findReservationOrder(reservation);
        return UserReservationResponse.createByContainWaitingOrder(reservation, reservationOrder);
    }
}
