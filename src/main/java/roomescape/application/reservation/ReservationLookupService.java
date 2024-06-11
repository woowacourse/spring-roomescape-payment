package roomescape.application.reservation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.reservation.dto.request.ReservationFilterRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationStatusResponse;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.infrastructure.reservation.ReservationSpec;

@Service
@Transactional(readOnly = true)
public class ReservationLookupService {
    private final ReservationRepository reservationRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;

    public ReservationLookupService(ReservationRepository reservationRepository,
                                    ReservationPaymentRepository reservationPaymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationPaymentRepository = reservationPaymentRepository;
    }

    public List<ReservationResponse> findByFilter(ReservationFilterRequest request) {
        Specification<Reservation> specification = ReservationSpec.where()
                .equalsMemberId(request.memberId())
                .equalsThemeId(request.themeId())
                .greaterThanOrEqualsStartDate(request.startDate())
                .lessThanOrEqualsEndDate(request.endDate())
                .build();

        return reservationRepository.findAll(specification)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findAllBookedReservations() {
        return reservationRepository.findAllBookedReservations()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findAllWaitingReservations() {
        return reservationRepository.findAllWaitingReservations()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationStatusResponse> getReservationStatusesByMemberId(long memberId) {
        return reservationRepository.findActiveReservationByMemberId(memberId).stream()
                .map(reservation -> {
                    Optional<ReservationPayment> optionalPayment =
                            reservationPaymentRepository.findByReservationId(reservation.getId());
                    String paymentKey = optionalPayment.map(ReservationPayment::getPaymentKey).orElse("");
                    long amount = optionalPayment.map(ReservationPayment::getAmount).orElse(0L);

                    return ReservationStatusResponse.of(
                            reservation,
                            reservationRepository.getWaitingCount(reservation),
                            paymentKey,
                            amount
                    );
                })
                .toList();
    }
}
