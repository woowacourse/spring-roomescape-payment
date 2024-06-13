package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Auth;
import roomescape.dto.MyReservationResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.ReservationService;

@RestController
@RequestMapping(value = "/reservations")
@Tag(name = "예약 API", description = "예약 관련 API 입니다.")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @Operation(summary = "예약 등록 API", description = "예약을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "예약 등록 성공")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Auth @Schema(description = "회원 ID") long memberId,
            @RequestBody ReservationRequest reservationRequest
    ) {
        reservationRequest = new ReservationRequest(
                reservationRequest.date(),
                memberId,
                reservationRequest.timeId(),
                reservationRequest.themeId()
        );
        ReservationResponse saved = reservationService.save(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + saved.id()))
                .body(saved);
    }

    @GetMapping
    @Operation(summary = "예약 목록 조회 API", description = "모든 예약 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/mine")
    @Operation(summary = "내 예약 목록 조회 API", description = "내 예약 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 예약 목록 조회 성공")
    public List<MyReservationResponse> findLoginMemberReservations(@Auth @Schema(description = "회원 ID") long memberId) {
        return reservationService.findByMemberId(memberId);
    }

    @DeleteMapping("/{reservationId}")
    @Operation(summary = "예약 취소 API", description = "예약을 취소합니다.")
    @ApiResponse(responseCode = "204", description = "예약 취소 성공")
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(description = "예약 ID") long reservationId,
            @Auth @Schema(description = "회원 ID") long memberId
    ) {
        reservationService.delete(reservationId, memberId);
        return ResponseEntity.noContent().build();
    }
}
