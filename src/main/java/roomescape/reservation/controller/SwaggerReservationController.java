package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.common.exception.error.ErrorResponse;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.controller.dto.MemberInfoResponse;
import roomescape.reservation.controller.dto.AvailableReservationTimeWebResponse;
import roomescape.reservation.controller.dto.CreateReservationWebRequest;
import roomescape.reservation.controller.dto.CreateReservationWithMemberIdWebRequest;
import roomescape.reservation.controller.dto.ReservationSearchWebRequest;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.controller.dto.ReservationWithStatusResponse;

@Tag(name = "예약 관리", description = "예약 API")
public interface SwaggerReservationController {

    @Operation(summary = "전체 예약 조회", description = "모든 예약을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회", content = @Content(schema = @Schema(implementation = ReservationWebResponse.class)))
    })
    List<ReservationWebResponse> getAll();

    @Operation(summary = "본인 예약 조회", description = "자신의 예약을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회", content = @Content(schema = @Schema(implementation = ReservationWithStatusResponse.class)))
    })
    List<ReservationWithStatusResponse> getAll(@Parameter(hidden = true) MemberInfo memberInfo);

    @Operation(summary = "예약 가능 시간 조회", description = "예약 가능 여부를 포함한 시간 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회", content = @Content(schema = @Schema(implementation = AvailableReservationTimeWebResponse.class)))
    })
    List<AvailableReservationTimeWebResponse> getAvailable(
            @Parameter(description = "확인 희망 날짜") final LocalDate date,
            @Parameter(description = "테마 ID") final Long themeId
    );

    @Operation(summary = "예약 추가", description = "회원의 예약을 추가합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "예약 생성 성공", content = @Content(schema = @Schema(implementation = ReservationWithStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ReservationWithStatusResponse> create(
            final CreateReservationWebRequest createReservationWebRequest,
            @Parameter(hidden = true) MemberInfo memberInfo
    );

    @Operation(summary = "관리자 예약 추가", description = "관리자가 특정 회원의 예약을 추가합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "예약 생성 성공", content = @Content(schema = @Schema(implementation = ReservationWebResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ReservationWebResponse> createReservationByAdmin(
            final CreateReservationWithMemberIdWebRequest createReservationWithMemberIdWebRequest
    );

    @Operation(summary = "예약 검색", description = "관리자가 특정 조건의 예약을 검색합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "검색 결과", content = @Content(schema = @Schema(implementation = ReservationWebResponse.class)))
    })
    ResponseEntity<List<ReservationWebResponse>> getReservationsByAdmin(
            final ReservationSearchWebRequest reservationSearchWebRequest
    );

    @Operation(summary = "예약 삭제", description = "ID에 해당하는 예약을 삭제합니다.", responses = {
            @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "예약이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(@Parameter(description = "예약 ID") final Long id);
}
