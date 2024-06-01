package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.MyReservationResponse;
import roomescape.dto.request.reservation.ReservationCriteriaRequest;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.exception.RoomescapeException;

@Service
public class ReservationService {
    private final ReservationFactory reservationFactory;
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationFactory reservationFactory, ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationFactory = reservationFactory;
    }

    @Transactional
    public ReservationResponse saveReservationByClient(LoginMember loginMember, ReservationRequest reservationRequest) {
        Reservation reservation = reservationFactory.createReservation(
                loginMember.id(),
                reservationRequest.date(),
                reservationRequest.timeId(),
                reservationRequest.themeId()
        );
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse saveWaitingByClient(LoginMember loginMember, WaitingRequest waitingRequest) {
        Reservation reservation = reservationFactory.createWaiting(
                loginMember.id(),
                waitingRequest.date(),
                waitingRequest.timeId(),
                waitingRequest.themeId()
        );
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse saveReservationByAdmin(AdminReservationRequest adminReservationRequest) {
        Reservation reservation = reservationFactory.createReservation(
                adminReservationRequest.memberId(),
                adminReservationRequest.date(),
                adminReservationRequest.timeId(),
                adminReservationRequest.themeId()
        );
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public void deleteById(long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND,
                        String.format("존재하지 않는 예약입니다. 요청 예약 id:%d", id)));
        reservationRepository.deleteById(reservation.getId());
        updateWaitingToReservation(reservation);
    }

    private void updateWaitingToReservation(Reservation reservation) {
        if (isWaitingUpdatableToReservation(reservation)) {
            reservationRepository.findFirstByDateAndTimeIdAndThemeIdAndStatus(
                    reservation.getDate(), reservation.getTime().getId(), reservation.getTheme().getId(), Status.WAITING
            ).ifPresent(Reservation::changePaymentWaiting);
        }
    }

    private boolean isWaitingUpdatableToReservation(Reservation reservation) {
        return reservation.getStatus() == Status.RESERVATION &&
                reservationRepository.existsByDateAndTimeIdAndThemeIdAndStatus(
                        reservation.getDate(), reservation.getTime().getId(),
                        reservation.getTheme().getId(), Status.WAITING
                );
    }

    public List<ReservationResponse> findAllByStatus(Status status) {
        List<Reservation> reservations = reservationRepository.findAllByStatus(status);
        return convertToReservationResponses(reservations);
    }

    private List<ReservationResponse> convertToReservationResponses(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findByCriteria(ReservationCriteriaRequest reservationCriteriaRequest) {
        Long themeId = reservationCriteriaRequest.themeId();
        Long memberId = reservationCriteriaRequest.memberId();
        LocalDate dateFrom = reservationCriteriaRequest.dateFrom();
        LocalDate dateTo = reservationCriteriaRequest.dateTo();
        return reservationRepository.findByCriteria(themeId, memberId, dateFrom, dateTo).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<MyReservationResponse> findMyReservations(Long memberId) {
        return reservationRepository.findAllByMemberIdOrderByDateAsc(memberId).stream()
                .map(reservation -> MyReservationResponse.of(reservation, getWaitingOrder(reservation)))
                .toList();
    }

    private long getWaitingOrder(Reservation reservation) {
        return reservationRepository.countByOrder(
                reservation.getId(), reservation.getDate(),
                reservation.getTime().getId(), reservation.getTheme().getId()
        );
    }
}
