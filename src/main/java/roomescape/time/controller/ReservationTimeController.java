package roomescape.time.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.global.dto.ErrorResponse;
import roomescape.time.controller.dto.AvailableReservationTimeRequest;
import roomescape.time.controller.dto.AvailableReservationTimeResponse;
import roomescape.time.controller.dto.CreateReservationTimeRequest;
import roomescape.time.controller.dto.ReservationTimeResponse;

@Tag(name = "예약 시간 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final TimeService timeService;

    public ReservationTimeController(final TimeService timeService) {
        this.timeService = timeService;
    }

    @Operation(summary = "예약 시간 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 시간을 성공적으로 생성한다."),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않아 예약 시간 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복되거나 조건에 맞지 않아 예약 시간 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 시간 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createTime(
            @RequestBody @Valid final CreateReservationTimeRequest request
    ) {
        ReservationTimeResponse response = timeService.createReservationTime(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "예약 시간 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 예약 시간 목록을 반환한다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 시간 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getTimes() {
        List<ReservationTimeResponse> responses = timeService.findAllReservationTimes();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 시간 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약 시간을 성공적으로 삭제한다."),
            @ApiResponse(responseCode = "404", description = "예약 시간을 찾을 수 없어 삭제에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 시간 삭제에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable final Long id) {
        timeService.deleteReservationTimeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 가능 시간 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 가능한 시간 목록을 반환한다."),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않아 예약 가능한 시간 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 가능한 시간 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableReservationTimes(
            @ModelAttribute @Valid final AvailableReservationTimeRequest request
    ) {
        List<AvailableReservationTimeResponse> responses = timeService.findAvailableReservationTimes(request);
        return ResponseEntity.ok(responses);
    }
}
