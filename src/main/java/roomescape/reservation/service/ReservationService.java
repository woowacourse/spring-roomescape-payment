package roomescape.reservation.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.controller.dto.AvailableReservationTimeWebResponse;
import roomescape.reservation.controller.dto.CreateReservationByAdminWebRequest;
import roomescape.reservation.controller.dto.CreateReservationWebRequest;
import roomescape.reservation.controller.dto.ReservationSearchWebRequest;
import roomescape.reservation.controller.dto.ReservationWaitWebResponse;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.controller.dto.ReservationWithStatusResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationWait;
import roomescape.reservation.service.converter.ReservationConverter;
import roomescape.reservation.service.converter.ReservationWaitConverter;
import roomescape.reservation.service.dto.AvailableReservationTimeServiceRequest;
import roomescape.reservation.service.dto.CreateReservationServiceRequest;
import roomescape.reservation.service.usecase.ReservationCommandUseCase;
import roomescape.reservation.service.usecase.ReservationQueryUseCase;
import roomescape.reservation.service.usecase.ReservationWaitCommandUseCase;
import roomescape.reservation.service.usecase.ReservationWaitQueryUseCase;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationQueryUseCase reservationQueryUseCase;
    private final ReservationCommandUseCase reservationCommandUseCase;
    private final ReservationWaitQueryUseCase reservationWaitQueryUseCase;
    private final ReservationWaitCommandUseCase reservationWaitCommandUseCase;
    private final PaymentRepository paymentRepository;

    public List<ReservationWebResponse> getAll() {
        return ReservationConverter.toDto(reservationQueryUseCase.getAll());
    }

    public List<ReservationWaitWebResponse> getAllReservationWait() {
        return ReservationWaitConverter.toDto(reservationWaitQueryUseCase.getAll());
    }

    public List<ReservationWithStatusResponse> findMyReservationsWithWaitingByMemberId(final Long loginMemberId) {
        final List<ReservationWithStatusResponse> allReservations = new ArrayList<>();
        allReservations.addAll(getByMemberId(loginMemberId));
        allReservations.addAll(getReservationWaitByMemberId(loginMemberId));

        return allReservations.stream()
                .sorted(Comparator.comparing(ReservationWithStatusResponse::getDate)
                        .thenComparing(ReservationWithStatusResponse::getTime)
                ).toList();
    }

    private List<ReservationWithStatusResponse> getByMemberId(final Long memberId) {
        List<Reservation> reservations = reservationQueryUseCase.getByMemberId(memberId);
        Map<Long, Payment> payments = getPaymentsByReservations(reservations);
        return reservations.stream()
                .map(reservation -> ReservationWithStatusResponse.of(
                        reservation,
                        payments.getOrDefault(reservation.getId(), null))
                ).toList();
    }

    private Map<Long, Payment> getPaymentsByReservations(List<Reservation> reservations) {
        List<Long> reservationIds = reservations.stream()
                .map(Reservation::getId)
                .toList();
        return paymentRepository.findAllByReservationIdIn(reservationIds)
                .stream()
                .collect(toMap(payment -> payment.getReservation().getId(), identity()));
    }

    private List<ReservationWithStatusResponse> getReservationWaitByMemberId(final Long memberId) {
        return reservationWaitQueryUseCase.getByMemberId(memberId).stream()
                .map(reservationWaitWithRank -> ReservationWithStatusResponse.of(
                        reservationWaitWithRank.reservationWait(),
                        reservationWaitWithRank.rank()))
                .toList();
    }

    public List<AvailableReservationTimeWebResponse> getAvailable(final LocalDate date, final Long id) {
        final AvailableReservationTimeServiceRequest serviceRequest = new AvailableReservationTimeServiceRequest(date,
                id);
        return reservationQueryUseCase.getTimesWithAvailability(serviceRequest).stream()
                .map(ReservationConverter::toWebDto)
                .toList();
    }

    public ReservationWebResponse create(final CreateReservationByAdminWebRequest createReservationByAdminWebRequest) {
        final Reservation reservation = reservationCommandUseCase.create(
                new CreateReservationServiceRequest(
                        createReservationByAdminWebRequest.memberId(),
                        createReservationByAdminWebRequest.date(),
                        createReservationByAdminWebRequest.timeId(),
                        createReservationByAdminWebRequest.themeId()
                )
        );
        return ReservationConverter.toDto(reservation);
    }

    public ReservationWaitWebResponse createReservationWait(
            final CreateReservationWebRequest createReservationWebRequest,
            final MemberInfo memberInfo
    ) {
        final ReservationWait reservationWait = reservationWaitCommandUseCase.create(
                new CreateReservationServiceRequest(
                        memberInfo.id(),
                        createReservationWebRequest.date(),
                        createReservationWebRequest.timeId(),
                        createReservationWebRequest.themeId()
                )
        );
        return ReservationWaitConverter.toDto(reservationWait);
    }

    public void delete(final Long id) {
        reservationCommandUseCase.delete(id);
    }

    public void deleteReservationWait(final Long id) {
        reservationWaitCommandUseCase.delete(id);
    }

    public List<ReservationWebResponse> search(final ReservationSearchWebRequest reservationSearchWebRequest) {
        final List<Reservation> reservations = reservationQueryUseCase.search(
                reservationSearchWebRequest.memberId(),
                reservationSearchWebRequest.themeId(),
                ReservationDate.from(reservationSearchWebRequest.from()),
                ReservationDate.from(reservationSearchWebRequest.to()));

        return reservations.stream()
                .map(ReservationConverter::toDto)
                .toList();
    }

}
