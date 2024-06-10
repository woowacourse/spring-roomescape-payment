package roomescape.reservation.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.exception.ErrorResponse;
import roomescape.reservation.dto.*;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.ReservationTime;
import roomescape.reservation.model.Theme;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.ReservationTimeService;
import roomescape.reservation.service.ThemeService;
import roomescape.reservation.service.WaitingService;

@RestController
@Tag(name = "관리자 예약", description = "관리자 예약 관련 API")
public class AdminReservationController {

    private final ReservationService reservationService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final WaitingService waitingService;

    public AdminReservationController(
            final ReservationService reservationService,
            final ReservationTimeService reservationTimeService,
            final ThemeService themeService,
            final WaitingService waitingService
    ) {
        this.reservationService = reservationService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.waitingService = waitingService;
    }

    @GetMapping("/admin/reservations")
    @Operation(summary = "예약 조회", description = "사용자, 테마, 날짜 조건에 맞는 예약 조회")
    @ApiResponse(responseCode = "200", description = "예약 조회 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<ReservationResponse> searchReservations(@ModelAttribute SearchReservationsRequest request) {
        return reservationService.searchReservations(request)
                .stream()
                .map(ReservationResponse::from)
                .toList();

    }

    @PostMapping("/admin/reservations")
    @Operation(summary = "예약 추가", description = "관리자 권한으로 예약 추가")
    @ApiResponse(responseCode = "201", description = "예약 추가 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ReservationResponse> saveReservation(@Valid @RequestBody final SaveReservationRequest request) {
        final Reservation savedReservation = reservationService.saveReservation(request, request.memberId());

        return ResponseEntity.created(URI.create("/reservations/" + savedReservation.getId()))
                .body(ReservationResponse.from(savedReservation));
    }

    @DeleteMapping("/admin/reservations/{reservation-id}")
    @Operation(summary = "예약 삭제", description = "관리자 권한으로 예약 삭제")
    @ApiResponse(responseCode = "204", description = "예약 삭제 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteReservation(@PathVariable("reservation-id") final Long reservationId) {
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/times")
    @Operation(summary = "예약 시간 추가")
    @ApiResponse(responseCode = "201", description = "예약 시간 추가 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ReservationTimeResponse> saveReservationTime(@RequestBody final SaveReservationTimeRequest request) {
        final ReservationTime savedReservationTime = reservationTimeService.saveReservationTime(request);

        return ResponseEntity.created(URI.create("/times/" + savedReservationTime.getId()))
                .body(ReservationTimeResponse.from(savedReservationTime));
    }

    @DeleteMapping("/admin/times/{reservation-time-id}")
    @Operation(summary = "예약 시간 삭제")
    @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteReservationTime(@PathVariable("reservation-time-id") final Long reservationTimeId) {
        reservationTimeService.deleteReservationTime(reservationTimeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/themes")
    @Operation(summary = "테마 추가")
    @ApiResponse(responseCode = "201", description = "테마 추가 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ThemeResponse> saveTheme(@RequestBody final SaveThemeRequest request) {
        final Theme savedTheme = themeService.saveTheme(request);

        return ResponseEntity.created(URI.create("/themes/" + savedTheme.getId()))
                .body(ThemeResponse.from(savedTheme));
    }

    @DeleteMapping("/admin/themes/{theme-id}")
    @Operation(summary = "테마 삭제")
    @ApiResponse(responseCode = "204", description = "테마 삭제 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteTheme(@PathVariable("theme-id") final Long themeId) {
        themeService.deleteTheme(themeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/waitings")
    @Operation(summary = "예약 대기 조회")
    @ApiResponse(responseCode = "200", description = "예약 대기 조회 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<WaitingResponse> getWaitings() {
        return waitingService.getWaitings()
                .stream()
                .map(WaitingResponse::from)
                .toList();
    }

    @DeleteMapping("/admin/waitings/{waiting-id}")
    @Operation(summary = "예약 대기 삭제")
    @ApiResponse(responseCode = "204", description = "예약 대기 삭제 성공")
    @ApiResponse(responseCode = "403", description = "유효하지 않은 권한 요청입니다.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteWaitings(@PathVariable("waiting-id") final Long waitingId) {
        waitingService.deleteWaiting(waitingId);
        return ResponseEntity.noContent().build();
    }
}
