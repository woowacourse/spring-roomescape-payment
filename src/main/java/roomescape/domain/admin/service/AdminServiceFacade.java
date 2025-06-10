package roomescape.domain.admin.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.admin.dto.AdminReservationRequest;
import roomescape.domain.admin.dto.AdminReservationResponse;
import roomescape.domain.admin.dto.ReservationSearchRequest;
import roomescape.domain.admin.dto.ReservationWaitingResponse;
import roomescape.domain.payment.service.PaymentService;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.waiting.entity.Waiting;

@RequiredArgsConstructor
@Service
public class AdminServiceFacade {

    private final AdminReservationService reservationService;
    private final AdminWaitingService waitingService;
    private final PaymentService paymentService;

    @Transactional
    public AdminReservationResponse saveByAdmin(final AdminReservationRequest adminReservationRequest) {
        final LocalDate date = adminReservationRequest.date();
        final Long themeId = adminReservationRequest.themeId();
        final Long timeId = adminReservationRequest.timeId();
        final Long memberId = adminReservationRequest.memberId();

        final Reservation savedReservation = reservationService.save(date, themeId, timeId, memberId);

        return AdminReservationResponse.from(savedReservation);
    }

    @Transactional
    public void deleteWaitingById(final Long id) {
        waitingService.deleteById(id);
    }

    @Transactional
    public void deleteById(final Long reservationId) {
        final Reservation reservation = reservationService.findById(reservationId);
        paymentService.deleteByReservationId(reservationId);
        reservationService.deleteById(reservationId);
        final boolean isExistWaiting = waitingService.existsByReservation(reservation);

        if (isExistWaiting) {
            final Waiting waiting = waitingService.findFirstByThemeAndDateAndTimeOrderByIdAsc(reservation);
            reservationService.save(
                    waiting.getDate(),
                    waiting.getTheme().getId(),
                    waiting.getTime().getId(),
                    waiting.getMember().getId()
            );
            waitingService.deleteById(waiting.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<AdminReservationResponse> findByInFromTo(final ReservationSearchRequest searchRequest) {
        final Long themeId = searchRequest.themeId();
        final Long memberId = searchRequest.memberId();
        final LocalDate from = searchRequest.dateFrom();
        final LocalDate to = searchRequest.dateTo();

        final List<Reservation> reservations = reservationService.findByInFromTo(themeId, memberId, from, to);

        return reservations.stream()
                .map(AdminReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationWaitingResponse> findAllWaitingReservations() {
        final List<Waiting> waitingReservations = waitingService.findAllWaitingReservations();

        return waitingReservations.stream()
                .map(ReservationWaitingResponse::from)
                .toList();
    }
}
