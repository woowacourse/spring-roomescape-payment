package roomescape.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.application.dto.AvailableReservationTimeServiceRequest;
import roomescape.reservation.application.dto.AvailableReservationTimeServiceResponse;
import roomescape.reservation.application.dto.CreateReservationServiceRequest;
import roomescape.reservation.application.dto.MyReservationsResponse;
import roomescape.reservation.application.dto.ReservationSearchRequest;
import roomescape.reservation.application.dto.ThemeToBookCountServiceResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.time.application.service.ReservationTimeQueryService;
import roomescape.time.domain.ReservationTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeQueryService reservationTimeQueryService;

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public List<AvailableReservationTimeServiceResponse> getTimesWithAvailability(
            final AvailableReservationTimeServiceRequest request) {
        List<ReservationTime> allTimes = reservationTimeQueryService.getAll();

        Set<Long> bookedTimeIds = new HashSet<>(reservationRepository.findTimeIdByParams(
                request.date(),
                request.themeId())
        );

        List<AvailableReservationTimeServiceResponse> responses = new ArrayList<>();

        for (final ReservationTime reservationTime : allTimes) {
            boolean isBooked = bookedTimeIds.contains(reservationTime.getId());
            responses.add(new AvailableReservationTimeServiceResponse(
                    reservationTime,
                    isBooked));
        }

        return responses;
    }

    public Optional<Reservation> findBySlot(final CreateReservationServiceRequest request) {
        return reservationRepository.findBySlot(request.date(), request.timeId(), request.themeId());
    }

    public List<ThemeToBookCountServiceResponse> getRanking(final ReservationDate startDate,
                                                            final ReservationDate endDate,
                                                            final int bookCount) {

        return reservationRepository.findThemesToBookedCount(startDate, endDate, bookCount)
                .stream()
                .map(current -> new ThemeToBookCountServiceResponse(current.theme(), current.bookedCount()))
                .toList();
    }

    public List<Reservation> getByParams(final ReservationSearchRequest request) {
        return reservationRepository.findAllByParams(
                request.userId(),
                request.themeId(),
                request.dateFrom(),
                request.dateTo()
        );
    }

    public boolean existsByTimeId(final Long timeId) {
        return reservationRepository.existsByParams(timeId);
    }

    public boolean existsByParams(final ReservationDate date,
                                  final Long timeId,
                                  final Long themeId) {
        return reservationRepository.existsByParams(date, timeId, themeId);
    }

    public List<MyReservationsResponse> findMyReservationsByUserId(final Long userId) {
        return reservationRepository.findMyReservationsByUserId(userId)
                .stream()
                .map(MyReservationsResponse::from)
                .toList();
    }
}
