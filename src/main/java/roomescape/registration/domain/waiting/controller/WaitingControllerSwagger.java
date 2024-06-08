package roomescape.registration.domain.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.registration.domain.waiting.dto.WaitingRequest;
import roomescape.registration.domain.waiting.dto.WaitingResponse;

import java.util.List;

import static roomescape.config.SwaggerConfig.JWT_TOKEN_COOKIE_AUTH;

@SecurityRequirement(name = JWT_TOKEN_COOKIE_AUTH)
@Tag(name = "Waiting", description = "대기 관련 기능을 제공하는 API")
public interface WaitingControllerSwagger {

    @Operation(
            summary = "대기 목록에 추가",
            description = "사용자 ID를 사용하여 대기 목록에 추가합니다.",
            requestBody = @RequestBody(
                    description = "대기 목록 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WaitingRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "대기 목록에 성공적으로 추가되었습니다.",
                            headers = @Header(name = "Location", description = "성공적으로 생성된 대기의 URI"),
                            content = @Content(
                                    schema = @Schema(implementation = WaitingResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<WaitingResponse> waitingSave(WaitingRequest waitingRequest, @Parameter(hidden = true) long memberId);

    @Operation(
            summary = "대기 목록 조회",
            description = "전체 대기 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "전체 대기 목록이 반환됩니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = WaitingResponse.class))
                            )
                    )
            }
    )
    ResponseEntity<List<WaitingResponse>> waitingList();

    @Operation(
            summary = "대기 목록에서 삭제",
            description = "지정된 대기 목록 ID를 가진 대기를 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "대기 목록이 성공적으로 삭제되었습니다.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    ResponseEntity<WaitingResponse> waitingRemove(long waitingId);
}
