package roomescape.reservationtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservationtime.dto.request.CreateReservationTimeRequest;
import roomescape.reservationtime.dto.response.CreateReservationTimeResponse;
import roomescape.reservationtime.dto.response.FindReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

import java.net.URI;
import java.util.List;

@Tag(name = "예약 시간 API", description = "예약 시간 관련 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 시간 생성 성공"),
            @ApiResponse(responseCode = "400", description = """
                    1. 예약 시간은 공백 문자가 불가능합니다.
                    2. 생성하려는 시간이 이미 존재합니다. 시간을 생성할 수 없습니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<CreateReservationTimeResponse> createReservationTime(@Valid @RequestBody final CreateReservationTimeRequest createReservationTimeRequest) {
        CreateReservationTimeResponse reservationTime = reservationTimeService.createReservationTime(createReservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + reservationTime.id())).body(reservationTime);
    }

    @Operation(summary = "예약 시간 목록 조회 API")
    @ApiResponse(responseCode = "200", description = "예약 시간 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<FindReservationTimeResponse>> getReservationTimes() {
        List<FindReservationTimeResponse> reservationTimeResponses = reservationTimeService.getReservationTimes();
        return ResponseEntity.ok(reservationTimeResponses);
    }

    @Operation(summary = "예약 시간 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 시간 조회 성공"),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 시간이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<FindReservationTimeResponse> getReservationTime(@PathVariable final Long id) {
        return ResponseEntity.ok(reservationTimeService.getReservationTime(id));
    }

    @Operation(summary = "예약 시간 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "식별자에 해당하는 시간을 사용 중인 예약이 존재합니다. 삭제가 불가능합니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 시간이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable final Long id) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
