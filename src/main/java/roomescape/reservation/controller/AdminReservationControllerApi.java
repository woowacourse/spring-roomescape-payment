package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.reservation.dto.request.CreateReservationByAdminRequest;
import roomescape.reservation.dto.response.CreateReservationResponse;
import roomescape.reservation.dto.response.FindAdminReservationResponse;

public abstract class AdminReservationControllerApi {

    @Operation(summary = "어드민의 회원 방탈출 예약 생성", description = "어드민 권한이 있는 유저가 회원의 방탈출 예약을 생성합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "회원 예약 생성 성공", content = @Content(schema = @Schema(implementation = CreateReservationResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(name = "해당 테마에 대한 예약이 존재하는 경우", value = "이미 2020-11-11의 행복한 라면가게 테마에는 11:00시의 예약이 존재하여 예약을 생성할 수 없습니다.")
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(name = "생성하려는 예약의 시간이 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 시간이 존재하지 않습니다."),
                    @ExampleObject(name = "생성하려는 예약의 테마가 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 테마가 존재하지 않습니다."),
                    @ExampleObject(name = "생성하려는 예약의 회원이 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 회원이 존재하지 않습니다.")}))}, parameters = @Parameter(name = "createReservationByAdminRequest", description = "생성하려는 예약의 정보를 담은 객체", required = true))
    abstract ResponseEntity<CreateReservationResponse> createReservationByAdmin(
            CreateReservationByAdminRequest createReservationByAdminRequest);

    @Operation(summary = "방탈출 전체 예약 조회", description = "저장된 모든 방탈출 예약 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "방탈출 전체 예약 조회 성공", content = @Content(schema = @Schema(implementation = FindAdminReservationResponse.class)))
    })
    abstract ResponseEntity<List<FindAdminReservationResponse>> getReservationsByAdmin();
}
