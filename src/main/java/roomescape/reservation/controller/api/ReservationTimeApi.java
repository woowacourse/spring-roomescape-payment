package roomescape.reservation.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import roomescape.reservation.dto.request.ReservationTimeRequest;
import roomescape.reservation.dto.response.ReservationTimeResponse;

@Tag(name = "ReservationTime", description = "예약 시간 API")
@RequestMapping("/times")
public interface ReservationTimeApi {

    @Operation(summary = "전체 예약 시간 조회", description = "전체 예약 시간을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 예약 시간 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<ReservationTimeResponse>> readAllReservationTimes();

    @Operation(summary = "예약 시간 생성", description = "예약 시간을 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 시간 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "중복 예약 시간 존재")
    })
    @PostMapping
    ResponseEntity<ReservationTimeResponse> create(@Valid @RequestBody final ReservationTimeRequest request);

    @Operation(summary = "예약 시간 삭제", description = "예약 시간을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "예약 시간이 존재하지 않음")
    })
    @DeleteMapping("/{timeId}")
    ResponseEntity<Void> delete(@PathVariable("timeId") final Long timeId);
}
