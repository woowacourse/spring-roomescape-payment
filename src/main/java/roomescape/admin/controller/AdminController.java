package roomescape.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.admin.domain.dto.AdminReservationRequestDto;
import roomescape.admin.domain.dto.SearchReservationRequestDto;
import roomescape.admin.service.AdminService;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.waiting.domain.dto.WaitingResponseDto;

import java.util.List;

@Tag(name = "관리자 API", description = "관리자 관련 API입니다.")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "관리자 예약", description = "관리자 계정으로 예약을 생성합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponseDto> createReservation(
            @RequestBody AdminReservationRequestDto requestDto) {
        ReservationResponseDto reservationResponseDto = adminService.createReservation(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponseDto);
    }

    @Operation(summary = "관리자 예약 검색", description = "관리자 계정으로 예약을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponseDto>> searchReservations(SearchReservationRequestDto requestDto) {
        List<ReservationResponseDto> reservationResponseDtos = adminService.searchReservations(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(reservationResponseDtos);
    }

    @Operation(summary = "관리자 예약 대기 조회", description = "관리자 계정으로 모든 예약 대기를 조회합니다.")
    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponseDto>> findAllWaitings() {
        List<WaitingResponseDto> responseDtos = adminService.findAllWaitings();
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @Operation(summary = "관리자 예약 대기 삭제", description = "관리자 계정으로 특정 예약 대기를 삭제합니다.")
    @DeleteMapping("/waitings/{id}")
    public HttpEntity<Void> delete(@PathVariable(value = "id") Long waitingId) {
        adminService.deleteWaitingById(waitingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
