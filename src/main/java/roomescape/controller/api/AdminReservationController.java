package roomescape.controller.api;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.request.SearchReservationFilterRequest;
import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.controller.dto.response.ReservationResponse;
import roomescape.global.exception.RoomescapeException;
import roomescape.service.AdminReservationService;

@Tag(name = "AdminReservation", description = "관리자 예약 관련 API")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    private final AdminReservationService adminReservationService;

    public AdminReservationController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }

    @Operation(summary = "모든 예약 조회", description = "모든 사용자의 예약을 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ReservationResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        List<ReservationResponse> response = adminReservationService.findAllReserved();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 예약 조회", description = "해당 기간에 해당 테마, 멤버인 예약들을 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ReservationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponse>> find(SearchReservationFilterRequest request) {
        List<ReservationResponse> response = adminReservationService.findAllByFilter(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 예약 대기 조회", description = "모든 사용자의 예약 대기을 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ReservationResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    @GetMapping("/standby")
    public ResponseEntity<List<ReservationResponse>> findAllStandby() {
        List<ReservationResponse> response = adminReservationService.findAllStandby();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 생성", description = "예약을 생성할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ReservationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> save(@Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse response = adminReservationService.reserve(request);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @Operation(summary = "예약 삭제", description = "예약을 삭제할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminReservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 대기 삭제", description = "예약 대기를 삭제할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    @DeleteMapping("/standby/{id}")
    public ResponseEntity<Void> deleteStandby(@PathVariable Long id) {
        adminReservationService.deleteStandby(id);
        return ResponseEntity.noContent().build();
    }
}
