package roomescape.reservation.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.common.response.ApiResponse;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.AdminReservationRequest;
import roomescape.reservation.application.dto.ReservationResponse;

@Tag(name = "예약 API For 관리자", description = "관리자만 호출 가능한 예약 관련 API입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("admin/reservations")
public class AdminReservationController {
    private final ReservationService reservationService;

    @Operation(summary = "예약 생성", description = "관리자가 사용자의 예약을 추가해주는 API입니다. 이 경우 예약 승인은 '관리자 승인'으로 처리됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody AdminReservationRequest request,
            @Parameter(hidden = true) @LoginMemberId Long adminId
    ) {
        ReservationResponse response = reservationService.createByAdmin(request, adminId);
        ApiResponse<ReservationResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "예약 조회", description = "모든 예약을 조회합니다. 쿼리 파라미터를 통해 필터링이 가능합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAll(
            @Parameter(description = "멤버 ID") @RequestParam(required = false) Long memberId,
            @Parameter(description = "테마 ID") @RequestParam(required = false) Long themeId,
            @Parameter(description = "조회 시작 날짜") @RequestParam(required = false) LocalDate from,
            @Parameter(description = "조회 끝 날짜") @RequestParam(required = false) LocalDate to
    ) {
        List<ReservationResponse> response = reservationService.findFiltered(memberId, themeId, from, to);
        ApiResponse<List<ReservationResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다. 예약 대기가 걸려있을 경우 해당 대기는 현장 결제로 자동 승인됩니다")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        reservationService.deleteById(id);
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
