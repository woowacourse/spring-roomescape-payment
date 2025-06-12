package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.common.exception.error.ErrorResponse;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.reservation.controller.dto.CreateWaitingWebRequest;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.controller.dto.ReservationWithStatusResponse;

@Tag(name = "예약 대기 관리", description = "예약 대기 API")
public interface SwaggerWaitingController {

    @Operation(summary = "예약 대기 추가", description = "회원의 예약 대기를 추가합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "예약 대기 생성 성공", content = @Content(schema = @Schema(implementation = ReservationWithStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ReservationWithStatusResponse> createWaiting(
            final CreateWaitingWebRequest createWaitingWebRequest,
            @Parameter(hidden = true) final MemberInfo memberInfo
    );

    @Operation(summary = "전체 예약 대기 조회", description = "전체 예약 대기 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReservationWebResponse.class)))
    })
    List<ReservationWebResponse> getAllWaiting();

    @Operation(summary = "예약 대기 삭제", description = "특정 ID의 예약 대기를 삭제합니다.", responses = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "예약 대기 ID가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteWaiting(@Parameter(description = "대기 ID") final Long id);

    @Operation(summary = "본인 예약 대기 삭제", description = "회원이 특정 ID에 해당하는 본인의 예약 대기를 삭제합니다.", responses = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "예약 대기 ID가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteWaiting(
            @Parameter(description = "대기 ID") final Long id,
            @Parameter(hidden = true) final MemberInfo memberInfo
    );
}
