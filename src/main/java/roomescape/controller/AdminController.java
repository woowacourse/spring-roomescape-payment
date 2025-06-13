package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import roomescape.annotation.CheckRole;
import roomescape.dto.request.AdminCreateReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;
import roomescape.entity.Reservation;
import roomescape.global.ReservationStatus;
import roomescape.global.Role;
import roomescape.service.ReservationService;

@Tag(name = "관리자", description = "관리자 전용 API")
@RestController
@RequestMapping("/admin")
@CheckRole(Role.ADMIN)
public class AdminController {

    private final ReservationService reservationService;

    public AdminController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "예약 목록 필터링 조회", description = "다양한 조건으로 예약 목록을 필터링하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservationsByFilter(
            @Parameter(description = "회원 ID") @RequestParam(required = false) Long memberId,
            @Parameter(description = "테마 ID") @RequestParam(required = false) Long themeId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate dateFrom,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) LocalDate dateTo) {
        List<Reservation> reservations = reservationService.findAllByFilter(memberId, themeId, dateFrom, dateTo);
        List<ReservationResponse> responses = reservations.stream()
                .map(ReservationResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "관리자 예약 생성", description = "관리자 권한으로 새로운 예약을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "예약 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservationByAdmin(
            @Parameter(description = "관리자 예약 생성 요청") @RequestBody @Valid AdminCreateReservationRequest request) {
        ReservationResponse response = reservationService.addReservationByAdmin(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/reservations/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "대기 예약 목록 조회", description = "모든 대기 상태의 예약 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "대기 예약 목록 조회 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @GetMapping("/reservations/waiting")
    public ResponseEntity<List<ReservationWaitResponse>> getWaitReservations() {
        List<ReservationWaitResponse> responses = reservationService.findAllByStatus(ReservationStatus.WAIT);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "대기 예약 승인", description = "대기 상태의 예약을 승인합니다.")
    @ApiResponse(responseCode = "200", description = "대기 예약 승인 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @PutMapping("/reservations/waiting/{id}")
    public ResponseEntity<Void> approveWaitReservation(
            @Parameter(description = "대기 예약 ID") @PathVariable("id") Long reservationId) {
        reservationService.approveWaitReservationByAdmin(reservationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "대기 예약 거절", description = "대기 상태의 예약을 거절합니다.")
    @ApiResponse(responseCode = "204", description = "대기 예약 거절 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @DeleteMapping("/reservations/waiting/{id}")
    public ResponseEntity<Void> rejectWaitReservation(
            @Parameter(description = "대기 예약 ID") @PathVariable("id") Long reservationId) {
        reservationService.rejectWaitReservationByAdmin(reservationId);
        return ResponseEntity.noContent().build();
    }
}
