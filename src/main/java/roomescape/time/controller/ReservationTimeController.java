package roomescape.time.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.ReservationTimeRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@Tag(name = "ReservationTime", description = "ReservationTime API")
@RestController
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 생성", description = "예약 시간을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 시간 생성 성공",
                    content = @Content(schema = @Schema(implementation = ReservationTimeResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 예약 시간 생성 시 실패")})
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> save(@RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse savedReservationTimeResponse = reservationTimeService.save(reservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + savedReservationTimeResponse.id()))
                .body(savedReservationTimeResponse);
    }

    @Operation(summary = "모든 예약 시간 조회", description = "모든 예약 시간을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 예약 시간 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReservationTimeResponse.class))),
            @ApiResponse(responseCode = "500", description = "(1) 데이터 베이스 통신 오류로 인한 실패")})
    @GetMapping("/times")
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll();
    }

    @Operation(summary = "모든 예약 가능 시간 조회", description = "모든 예약 가능 시간을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 예약 가능 시간 조회 성공",
                    content = @Content(schema = @Schema(implementation = AvailableTimeResponse.class))),
            @ApiResponse(responseCode = "400", description = "(1) 적합하지 않은 인자로 예약 가능 시간 조회 시 실패")})
    @GetMapping("/times/available")
    public List<AvailableTimeResponse> findAvailableTimeWith(
            @Parameter(description = "예약 날짜", required = true) @RequestParam LocalDate date,
            @Parameter(description = "테마 ID", required = true) @RequestParam long themeId) {
        return reservationTimeService.findByThemeAndDate(date, themeId);
    }

    @Operation(summary = "예약 시간 삭제", description = "예약 시간을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공"),
            @ApiResponse(responseCode = "500", description = "(1) 데이터 베이스 통신 오류로 인한 실패")})
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(required = true, name = "id") @PathVariable long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
