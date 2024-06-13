package roomescape.reservationtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.reservationtime.dto.request.CreateReservationTimeRequest;
import roomescape.reservationtime.dto.response.CreateReservationTimeResponse;
import roomescape.reservationtime.dto.response.FindReservationTimeResponse;

public abstract class ReservationTimeControllerApi {

    @Operation(summary = "방탈출 시간대 생성", description = "새로운 방탈출 시간대를 생성합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "내 시간대 생성 성공", content = @Content(schema = @Schema(implementation = CreateReservationTimeResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(name = "이미 서버에 존재하는 시간대를 생성하려는 경우.", value = "생성하려는 시간 11:00가 이미 존재합니다. 시간을 생성할 수 없습니다.")
            }))},
            parameters = @Parameter(name = "createReservationTimeRequest", description = "생성하려는 시간 정보를 담은 객체", required = true))
    abstract ResponseEntity<CreateReservationTimeResponse> createReservationTime(
            CreateReservationTimeRequest createReservationTimeRequest);

    @Operation(summary = "방탈출 시간대 목록 조회", description = "방탈출 시간대 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "시간대 목록 조회 성공", content = @Content(schema = @Schema(implementation = FindReservationTimeResponse.class)))
    })
    abstract ResponseEntity<List<FindReservationTimeResponse>> getReservationTimes();

    @Operation(summary = "방탈출 시간대 단건 조회 조회", description = "방탈출 시간대 하나를 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "특정 시간대 조회 성공", content = @Content(schema = @Schema(implementation = FindReservationTimeResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(name = "존재하지 않는 시간대", description = "서버에 존재하지 않는 시간대를 조회하려는 경우.", value = "식별자 1에 해당하는 시간이 존재하지 않습니다.")
            }))},
            parameters = @Parameter(name = "id", description = "조회하려는 시간대 식별자", required = true))
    abstract ResponseEntity<FindReservationTimeResponse> getReservationTime(Long id);

    @Operation(summary = "방탈출 시간대 삭제", description = "방탈출 시간대를 삭제합니다.", responses = {
            @ApiResponse(responseCode = "204", description = "시간대 삭제 성공"),
            @ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(name = "해당 시간대에 대한 예약이 존재하는 경우", value = "식별자 1인 시간을 사용 중인 예약이 존재합니다. 삭제가 불가능합니다."))),
            @ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(name = "거절하려는 시간대의 식별자 대한 정보가 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 시간이 존재하지 않습니다. 삭제가 불가능합니다.")))},
            parameters = @Parameter(name = "id", description = "삭제하려는 시간대 식별자", required = true, example = "1"))
    abstract ResponseEntity<Void> deleteReservationTime(Long id);
}
