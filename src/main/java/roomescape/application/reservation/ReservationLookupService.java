package roomescape.application.reservation;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.reservation.dto.request.ReservationFilterRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationStatusResponse;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.infrastructure.reservation.ReservationSpec;

@Service
@Transactional(readOnly = true)
public class ReservationLookupService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public ReservationLookupService(ReservationRepository reservationRepository,
                                    PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
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
        return reservationRepository.findActiveReservationByMemberId(memberId)
                .stream()
                .map(this::extractResponse)
                .toList();
    }

    private ReservationStatusResponse extractResponse(Reservation reservation) {
        Theme theme = reservation.getTheme();
        ReservationTime time = reservation.getTime();
        long waitingCount = reservationRepository.getWaitingCount(reservation);

        ReservationStatusResponse response = new ReservationStatusResponse(
                reservation.getId(),
                theme.getName(),
                reservation.getDate(),
                time.getStartAt(),
                waitingCount
        );

        return paymentRepository.findByOrderId(reservation.getOrderId())
                .map(response::withPayment)
                .orElse(response);
    }
}
