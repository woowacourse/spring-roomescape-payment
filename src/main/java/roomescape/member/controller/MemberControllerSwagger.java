package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.member.dto.MemberResponse;
import roomescape.registration.dto.RegistrationInfoDto;

import java.util.List;

import static roomescape.config.SwaggerConfig.JWT_TOKEN_COOKIE_AUTH;

@SecurityRequirement(name = JWT_TOKEN_COOKIE_AUTH)
@Tag(name = "Member", description = "회원 관련 기능을 제공하는 API")
public interface MemberControllerSwagger {

    @Operation(
            summary = "회원 목록 조회",
            description = "모든 회원의 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 목록이 반환됩니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MemberResponse.class))
                            )
                    )
            }
    )
    List<MemberResponse> memberIdList();

    @Operation(
            summary = "내 예약 정보 목록 조회",
            description = "로그인한 회원의 예약 및 대기 목록 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원의 예약 및 대기 정보 목록이 반환됩니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = RegistrationInfoDto.class))
                            )
                    )
            }
    )
    List<RegistrationInfoDto> memberReservationList(@Parameter(hidden = true) long memberId);
}
