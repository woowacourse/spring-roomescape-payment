package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.auth.service.dto.LoginMember;
import roomescape.common.exception.BadRequestException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.service.dto.request.FilteringReservationRequest;
import roomescape.reservation.service.dto.response.MyReservationsResponse;
import roomescape.reservation.service.dto.response.ReservationResponse;
import roomescape.reservation.service.dto.response.ReservationTimeWithBookedResponse;
import roomescape.waiting.repository.WaitingRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final WaitingRepository waitingRepository;

    public ReservationQueryService(
            final ReservationRepository reservationRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final WaitingRepository waitingRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.waitingRepository = waitingRepository;
    }

    public List<ReservationResponse> getAll() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<MyReservationsResponse> getAllLoginMemberReservations(LoginMember loginMember) {
        ArrayList<MyReservationsResponse> results = new ArrayList<>(
                reservationRepository.findAllByMemberId(loginMember.id())
                        .stream()
                        .map(MyReservationsResponse::from)
                        .toList()
        );
        results.addAll(
                waitingRepository.findAllWaitingInfoByMemberId(loginMember.id())
                        .stream()
                        .map(MyReservationsResponse::from)
                        .toList()
        );
        return Collections.unmodifiableList(results);
    }

    public List<ReservationResponse> findReservationByFiltering(final FilteringReservationRequest request) {
        Long themeId = request.themeId();
        Long memberId = request.memberId();
        LocalDate dateFrom = request.dateFrom();
        LocalDate dateTo = request.dateTo();

        validateDateFromTo(dateFrom, dateTo);

        return reservationRepository.findByThemeIdAndMemberIdAndDateBetween(themeId, memberId, dateFrom, dateTo)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private void validateDateFromTo(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new BadRequestException("종료 날짜는 시작 날짜보다 앞설 수 없습니다.");
        }
    }

    public List<ReservationTimeWithBookedResponse> getReservationTimesWithBooked(final LocalDate date, final Long themeId) {
         return reservationTimeRepository.findAllWithBooked(date, themeId)
                 .stream()
                 .map(ReservationTimeWithBookedResponse::from)
                 .toList();
    }
}
