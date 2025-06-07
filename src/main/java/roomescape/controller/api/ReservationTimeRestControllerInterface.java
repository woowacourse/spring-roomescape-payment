package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.domain.time.dto.ReservationTimeRequest;
import roomescape.domain.time.dto.ReservationTimeResponse;

@Tag(name = "ReservationTime", description = "예약 시간 관련 API")
@RequestMapping("/times")
public interface ReservationTimeRestControllerInterface {

    @Operation(
            summary = "예약 시간 생성", description = "예약 시간 생성 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 시간 생성 성공"),
            @ApiResponse(responseCode = "400", description = "예약 시간 생성하기 위한 요청이 잘못되었습니다."),
            @ApiResponse(responseCode = "409", description = "중복된 데이터가 존재합니다."),
            @ApiResponse(responseCode = "422", description = "과거 날짜는 예약 할 수 없습니다.")
    })
    @PostMapping
    ResponseEntity<ReservationTimeResponse> createReservationTime(
            @RequestBody final ReservationTimeRequest request
    );

    @Operation(
            summary = "예약 시간 조회", description = "예약 시간을 조회 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 시간 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<ReservationTimeResponse>> getReservationTimes();

    @Operation(
            summary = "예약 시간 삭제", description = "예약 시간을 삭제 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "예약 시간을 찾을 수 없습니다.")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteReservationTime(@PathVariable final Long id);
}
