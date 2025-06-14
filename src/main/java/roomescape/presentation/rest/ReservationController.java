package roomescape.presentation.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ReservationService;
import roomescape.application.RoomescapeService;
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.domain.reservation.ReservationSearchFilter;
import roomescape.exception.AuthorizationException;
import roomescape.presentation.request.CreateReservationRequest;
import roomescape.presentation.response.ReservationResponse;

@Tag(name = "Reservation", description = "예약 관련 API")
@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomescapeService roomescapeService;

    @Operation(summary = "예약하기", description = "사용자가 날짜, 테마, 시간, 결제수단 선택 후 예약을 요청합니다.")
    @PostMapping
    @ResponseStatus(CREATED)
    public ReservationResponse reserve(
            final AuthenticationInfo authenticationInfo,
            @RequestBody @Valid final CreateReservationRequest request
    ) {
        var reservation = roomescapeService.reserveAndPay(authenticationInfo.id(), request);
        return ReservationResponse.from(reservation);
    }

    @Operation(summary = "예약대기 신청", description = "이미 예약이 있을 때 사용자가 예약대기를 신청합니다.")
    @PostMapping("/wait")
    @ResponseStatus(CREATED)
    public ReservationResponse waitFor(
            final AuthenticationInfo authenticationInfo,
            @RequestBody @Valid final CreateReservationRequest request
    ) {
        var reservation = reservationService.waitFor(authenticationInfo.id(), request.date(), request.timeId(), request.themeId());
        return ReservationResponse.from(reservation);
    }

    @Operation(summary = "예약 조회", description = "관리자가 선택적인 검색 조건에 따라 예약을 조회합니다.")
    @GetMapping
    public List<ReservationResponse> getAllReservations(
            @RequestParam(name = "themeId", required = false) final Long themeId,
            @RequestParam(name = "userId", required = false) final Long userId,
            @RequestParam(name = "dateFrom", required = false) final LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) final LocalDate dateTo
    ) {
        var searchFilter = new ReservationSearchFilter(themeId, userId, dateFrom, dateTo);
        var reservations = reservationService.findAllReservations(searchFilter);
        return ReservationResponse.from(reservations);
    }

    @Operation(summary = "예약 취소 / 예약대기 거절", description = "관리자가 예약을 취소하거나, 예약대기를 거절합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(
        final AuthenticationInfo authenticationInfo,
        @PathVariable("id") final long id
    ) {
        if (!authenticationInfo.isAdmin()) {
            throw new AuthorizationException("관리자에게만 허용된 작업입니다.");
        }
        reservationService.removeById(id);
    }

    @Operation(summary = "예약대기 취소", description = "사용자가 예약대기를 취소합니다.")
    @DeleteMapping("/wait/{id}")
    @ResponseStatus(NO_CONTENT)
    public void cancelWaiting(
        final AuthenticationInfo authenticationInfo,
        @PathVariable("id") final long reservationId
    ) {
        var userId = authenticationInfo.id();
        reservationService.cancelWaiting(userId, reservationId);
    }
}
