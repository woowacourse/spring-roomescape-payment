package roomescape.controller.api.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.request.SearchReservationFilterRequest;
import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.controller.dto.response.ReservationResponse;

@Tag(name = "AdminReservation", description = "관리자 예약 관련 API")
public interface AdminReservationApiDocs {
    @Operation(summary = "모든 예약 조회", description = "모든 사용자의 예약을 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(schema = @Schema(implementation = ReservationResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<List<ReservationResponse>> findAll();

    @Operation(summary = "특정 예약 조회", description = "해당 기간에 해당 테마, 멤버인 예약들을 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(schema = @Schema(implementation = ReservationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<List<ReservationResponse>> find(SearchReservationFilterRequest request);

    @Operation(summary = "모든 예약 대기 조회", description = "모든 사용자의 예약 대기을 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(schema = @Schema(implementation = ReservationResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<List<ReservationResponse>> findAllStandby();

    @Operation(summary = "예약 생성", description = "예약을 생성할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = {@Content(schema = @Schema(implementation = ReservationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<ReservationResponse> save(CreateReservationRequest request);

    @Operation(summary = "예약 삭제", description = "예약을 삭제할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<Void> delete(Long id);

    @Operation(summary = "예약 대기 삭제", description = "예약 대기를 삭제할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<Void> deleteStandby(Long id);
}
