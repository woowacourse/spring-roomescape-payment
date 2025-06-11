package roomescape.reservationTime.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.reservationTime.dto.request.ReservationTimeRequest;
import roomescape.reservationTime.dto.request.TimeConditionRequest;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import roomescape.reservationTime.dto.response.TimeConditionResponse;

import java.util.List;

@Tag(name = "예약 시간", description = "예약 시간 관련 API")
public interface ReservationTimeControllerDocs {

    @Operation(summary = "예약 시간 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 시간 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "중복된 예약 시간",
                            value = "{\"message\": \"이미 존재하는 예약 시간입니다.\"}"
                    )
            }))
    })
    ResponseEntity<ReservationTimeResponse> createReservationTime(ReservationTimeRequest request);

    @Operation(summary = "예약 시간 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 시간 목록 조회 성공")
    })
    ResponseEntity<List<ReservationTimeResponse>> getReservationTimes();

    @Operation(summary = "조건별 예약 시간 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조건별 예약 시간 조회 성공")
    })
    ResponseEntity<List<TimeConditionResponse>> getReservationTimes(TimeConditionRequest request);

    @Operation(summary = "예약 시간 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "예약이 존재하는 예약 시간",
                            value = "{\"message\": \"해당 예약 시간에 예약이 존재합니다.\"}"
                    )
            })),
            @ApiResponse(responseCode = "404", description = "예약 시간을 찾을 수 없음", content = @Content(examples = {
                    @ExampleObject(
                            name = "존재하지 않는 예약 시간",
                            value = "{\"message\": \"해당 예약 시간을 찾을 수 없습니다.\"}"
                    )
            }))
    })
    ResponseEntity<Void> deleteReservationTimeById(Long id);
} 