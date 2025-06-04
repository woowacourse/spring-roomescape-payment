package roomescape.reservation.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.AdminReservationRequest;
import roomescape.reservation.application.dto.AdminReservationSearchRequest;
import roomescape.reservation.application.dto.ReservationResponse;

@RestController
@AllArgsConstructor
@RequestMapping("admin/reservations")
public class AdminReservationController {
    private final ReservationService reservationService;

    @Operation(
            summary = "관리자 예약 생성",
            description = "관리자가 특정 유저와 시간 슬롯을 지정하여 예약을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody AdminReservationRequest request
    ) {
        ReservationResponse response = reservationService.createByAdmin(request);
        ApiResponse<ReservationResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(
            summary = "관리자 예약 목록 조회",
            description = "필터 조건(회원 ID, 시간 ID)을 바탕으로 예약 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAll(
            @ModelAttribute AdminReservationSearchRequest request
    ) {
        List<ReservationResponse> response = reservationService.findFiltered(request);
        ApiResponse<List<ReservationResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "예약 삭제",
            description = "예약 ID를 통해 해당 예약을 삭제합니다. 삭제 시 NO_CONTENT 상태를 반환합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        reservationService.deleteById(id);
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
