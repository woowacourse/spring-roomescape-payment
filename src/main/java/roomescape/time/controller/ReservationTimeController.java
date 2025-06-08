package roomescape.time.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.exception.dto.ErrorResponse;
import roomescape.time.dto.request.ReservationTimeRequest;
import roomescape.time.dto.response.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@Tag(name = "예약 시간", description = "예약 시간 API")
@Slf4j
@RequestMapping("/times")
@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 생성", description = "예약 시간을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 시간 생성 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 시간",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> create(@Valid @RequestBody final ReservationTimeRequest request) {
        log.info("예약 시간 생성 요청: startAt={}", request.startAt());
        ReservationTimeResponse response = reservationTimeService.create(request);
        return ResponseEntity.created(URI.create("/times/" + response.id()))
                .body(response);
    }

    @Operation(summary = "전체 예약 시간 조회", description = "등록된 모든 예약 시간을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 시간 조회 성공",
            content = @Content(
                    mediaType = "application/json"))
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> readAllReservationTimes() {
        List<ReservationTimeResponse> responses = reservationTimeService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 시간 삭제", description = "예약 시간 ID를 기준으로 예약 시간을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(responseCode = "400", description = "예약이 존재하는 시간은 삭제할 수 없습니다.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약 시간",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{timeId}")
    public ResponseEntity<Void> delete(@Parameter(description = "삭제할 시간 ID", example = "1")
                                       @PathVariable("timeId") final Long timeId) {
        log.info("예약 시간 삭제 요청: timeId={}", timeId);
        reservationTimeService.delete(timeId);
        return ResponseEntity.noContent().build();
    }
}
