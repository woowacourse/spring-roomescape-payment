package roomescape.time.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
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
import roomescape.time.controller.dto.AvailableReservationTimeRequest;
import roomescape.time.controller.dto.AvailableReservationTimeResponse;
import roomescape.time.controller.dto.CreateReservationTimeRequest;
import roomescape.time.controller.dto.ReservationTimeResponse;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final TimeService timeService;

    public ReservationTimeController(final TimeService timeService) {
        this.timeService = timeService;
    }

    @Operation(summary = "예약 시간대 생성 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "예약 시간대 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "이미 존재하는 시간입니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
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

    @Operation(summary = "예약 시간대 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 시간대 조회 성공"
            )
    })
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getTimes() {
        List<ReservationTimeResponse> responses = timeService.findAllReservationTimes();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 시간대 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "예약 시간대 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 시간에 예약이 존재하여 삭제할 수 없습니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable final Long id) {
        timeService.deleteReservationTimeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 가능 시간대 조회 API", description = "사용자가 예약할 수 있는 시간대와 이미 예약이 완료된 시간대를 구분하여 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 가능 시간대 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 테마가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
    })
    @GetMapping("/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableReservationTimes(
            @ModelAttribute @Valid final AvailableReservationTimeRequest request
    ) {
        List<AvailableReservationTimeResponse> responses = timeService.findAvailableReservationTimes(request);
        return ResponseEntity.ok(responses);
    }
}
