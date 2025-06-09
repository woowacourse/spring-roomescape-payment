package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import roomescape.annotation.CheckRole;
import roomescape.domain.ReservationSlot;
import roomescape.domain.ReservationSlots;
import roomescape.dto.request.AvailableTimeRequest;
import roomescape.dto.request.CreateReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationTimeSlotResponse;
import roomescape.entity.ReservationTime;
import roomescape.global.Role;
import roomescape.service.ReservationTimeService;

@Tag(name = "예약 시간", description = "예약 시간 관리 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 목록 조회", description = "모든 예약 가능 시간을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 시간 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimes() {
        List<ReservationTime> reservationTimes = reservationTimeService.findAll();
        List<ReservationTimeResponse> responses = reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 가능 시간 조회", description = "특정 날짜와 테마에 대한 예약 가능 시간을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 가능 시간 조회 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @GetMapping("/available")
    public ResponseEntity<List<ReservationTimeSlotResponse>> getAvailableReservationTimes(
            @Parameter(description = "예약 가능 시간 조회 요청") @ModelAttribute @Valid AvailableTimeRequest request) {
        ReservationSlots reservationSlotTimes = reservationTimeService.getReservationSlots(request);
        List<ReservationSlot> reservationSlots = reservationSlotTimes.getReservationSlots();
        List<ReservationTimeSlotResponse> responses = reservationSlots.stream()
                .map(ReservationTimeSlotResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 시간 추가", description = "관리자 권한으로 새로운 예약 시간을 추가합니다.")
    @ApiResponse(responseCode = "201", description = "예약 시간 추가 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @PostMapping
    @CheckRole(value = Role.ADMIN)
    public ResponseEntity<ReservationTimeResponse> addReservationTime(
            @Parameter(description = "예약 시간 생성 요청") @RequestBody @Valid CreateReservationTimeRequest request) {
        ReservationTime reservationTime = reservationTimeService.addReservationTime(request);
        ReservationTimeResponse response = ReservationTimeResponse.from(reservationTime);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "예약 시간 삭제", description = "관리자 권한으로 예약 시간을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공")
    @ApiResponse(responseCode = "400", description = "예약이 있는 시간은 삭제할 수 없음")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @DeleteMapping("/{id}")
    @CheckRole(value = Role.ADMIN)
    public ResponseEntity<Void> deleteReservationTime(
            @Parameter(description = "예약 시간 ID") @PathVariable Long id) {
        reservationTimeService.deleteReservationTime(id);

        return ResponseEntity.noContent().build();
    }
}
