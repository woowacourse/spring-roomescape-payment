package roomescape.reservation.domain.specification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import roomescape.auth.domain.AuthInfo;
import roomescape.reservation.controller.dto.MyReservationResponse;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReservationSpecification {

    List<ReservationResponse> reservations(
            Long themeId,
            Long memberId,
            LocalDate startDate,
            LocalDate endDate
    );

    ReservationResponse create(AuthInfo authInfo, @Valid ReservationRequest reservationRequest);

    void delete(AuthInfo authInfo, @Min(1) long reservationMemberId);

    List<MyReservationResponse> getMyReservations(AuthInfo authInfo);

    List<ReservationResponse> getWaiting();

    void deleteWaiting(AuthInfo authInfo, @Min(1) long reservationMemberId);
}
