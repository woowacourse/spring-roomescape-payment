package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.core.AuthenticationPrincipal;
import roomescape.auth.domain.AuthInfo;
import roomescape.reservation.dto.request.CreateMyReservationRequest;
import roomescape.reservation.dto.response.CreateReservationResponse;
import roomescape.reservation.dto.response.FindAvailableTimesResponse;
import roomescape.reservation.dto.response.FindReservationResponse;
import roomescape.reservation.dto.response.FindReservationWithPaymentResponse;
import roomescape.reservation.service.ReservationService;

@Tag(name = "예약 API", description = "예약 관련 API")
@RestController
@RequestMapping
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "예약 생성 API")
    @PostMapping("/reservations")
    public ResponseEntity<CreateReservationResponse> createReservation(
            @AuthenticationPrincipal AuthInfo authInfo,
            @Valid @RequestBody CreateMyReservationRequest createReservationRequest) {
        CreateReservationResponse createReservationResponse =
                reservationService.createMyReservationWithPayment(authInfo, createReservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + createReservationResponse.id()))
                .body(createReservationResponse);
    }

    @Operation(summary = "예약 조회 API")
    @GetMapping("/reservations/{id}")
    public ResponseEntity<FindReservationResponse> getReservation(@PathVariable final Long id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

    @Operation(summary = "예약 시간 조회 API")
    @GetMapping("/reservations/times")
    public ResponseEntity<List<FindAvailableTimesResponse>> getAvailableTimes(@RequestParam LocalDate date,
                                                                              @RequestParam Long themeId) {
        return ResponseEntity.ok(reservationService.getAvailableTimes(date, themeId));
    }

    @Operation(summary = "예약 검색 API")
    @GetMapping("/reservations/search")
    public ResponseEntity<List<FindReservationResponse>> searchBy(@RequestParam(required = false) Long themeId,
                                                                  @RequestParam(required = false) Long memberId,
                                                                  @RequestParam(required = false) LocalDate dateFrom,
                                                                  @RequestParam(required = false) LocalDate dateTo) {
        return ResponseEntity.ok(reservationService.searchBy(themeId, memberId, dateFrom, dateTo));
    }

    @Operation(summary = "예약 삭제 API")
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal AuthInfo authInfo,
                                                  @PathVariable Long id) {
        reservationService.deleteReservation(authInfo, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 예약 목록 조회 API")
    @GetMapping("/members/reservations")
    public ResponseEntity<List<FindReservationWithPaymentResponse>> getReservations(
            @AuthenticationPrincipal AuthInfo authInfo) {
        return ResponseEntity.ok(reservationService.getReservations(authInfo));
    }
}
