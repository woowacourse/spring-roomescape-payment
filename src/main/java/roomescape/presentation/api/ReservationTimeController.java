package roomescape.presentation.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import roomescape.application.ReservationTimeService;
import roomescape.presentation.dto.request.AvailableReservationTimeRequest;
import roomescape.presentation.dto.request.ReservationTimeCreateRequest;
import roomescape.presentation.dto.response.AvailableReservationTimeResponse;
import roomescape.presentation.dto.response.ErrorResponse;
import roomescape.presentation.dto.response.ReservationTimeResponse;

import java.util.List;

@Tag(name = "예약 시간 관련 기능 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Hidden
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getTimes() {
        List<ReservationTimeResponse> responses = reservationTimeService.getReservationTimes();

        return ResponseEntity.ok(responses);
    }

    @Hidden
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createTime(@RequestBody @Valid ReservationTimeCreateRequest request) {
        ReservationTimeResponse response = reservationTimeService.createReservationTime(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Hidden
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable Long id) {
        reservationTimeService.deleteReservationTimeById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    @Operation(summary = "예약 가능 시간 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "선택한 id의 테마가 존재하지 않는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableReservationTimes(
            @ModelAttribute @Valid AvailableReservationTimeRequest request
    ) {
        List<AvailableReservationTimeResponse> responses =
                reservationTimeService.getAvailableReservationTimes(request);

        return ResponseEntity.ok(responses);
    }
}
