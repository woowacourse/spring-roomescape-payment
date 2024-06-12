package roomescape.controller.api.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.CreateTimeRequest;
import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.controller.dto.response.TimeResponse;

@Tag(name = "AdminReservationTime", description = "관리자만 접근할 수 있는 방탈출 예약 가능 시간 관련 API")
public interface AdminReservationTimeApiDocs {
    @Operation(summary = "모든 시간 조회", description = "모든 예약 가능 시간을 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = TimeResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<List<TimeResponse>> findAll();

    @Operation(summary = "시간 생성", description = "예약 가능 시간을 생성할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = TimeResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<TimeResponse> save(CreateTimeRequest request);

    @Operation(summary = "시간 삭제", description = "예약 가능 시간을 삭제할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<Void> delete(Long id);
}
