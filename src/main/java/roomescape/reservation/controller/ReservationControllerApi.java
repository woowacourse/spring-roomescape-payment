package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.auth.domain.AuthInfo;
import roomescape.reservation.dto.request.CreateMyReservationRequest;
import roomescape.reservation.dto.response.CreateReservationResponse;
import roomescape.reservation.dto.response.FindAvailableTimesResponse;
import roomescape.reservation.dto.response.FindReservationResponse;
import roomescape.reservation.dto.response.FindReservationWithPaymentResponse;

public abstract class ReservationControllerApi {

    @SecurityRequirement(name = "쿠키 인증 토큰")
    @Operation(summary = "회원 방탈출 예약 생성", description = "회원의 방탈출 예약을 생성합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "회원 예약 생성 성공", content = @Content(schema = @Schema(implementation = CreateReservationResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(name = "이미 존재하는 예약", description = "해당 테마에 대한 예약이 존재하는 경우", value = "이미 2020-11-11의 행복한 라면가게 테마에는 11:00시의 예약이 존재하여 예약을 생성할 수 없습니다.")
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(name = "생성하려는 예약의 시간이 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 시간이 존재하지 않습니다."),
                    @ExampleObject(name = "생성하려는 예약의 테마가 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 테마가 존재하지 않습니다."),
                    @ExampleObject(name = "생성하려는 예약의 회원이 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 회원이 존재하지 않습니다.")}))}, parameters = @Parameter(name = "createReservationRequest", description = "생성하려는 예약의 정보를 담은 객체", required = true))
    abstract ResponseEntity<CreateReservationResponse> createReservation(AuthInfo authInfo,
                                                                         CreateMyReservationRequest createReservationRequest);

    @Operation(summary = "방탈출 예약 단건 조회", description = "식별자에 해당하는 예약 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "방탈출 예약 단건 조회 성공", content = @Content(schema = @Schema(implementation = FindReservationResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(name = "존재하지 않는 예약", description = "서버에 존재하지 않는 예약을 조회하려는 경우.", value = "식별자 1에 해당하는 예약 존재하지 않습니다.")
            }))}, parameters = @Parameter(name = "id", description = "조회하려는 예약 식별자", required = true))
    abstract ResponseEntity<FindReservationResponse> getReservation(Long id);

    @Operation(summary = "테마, 날짜에 해당하는 방탈출 예약 가능한 시간 목록 조회", description = "해당하는 테마, 날짜에 현재 예약 가능한 시간 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "방탈출 예약 가능한 시간 목록 조회 성공", content = @Content(schema = @Schema(implementation = FindAvailableTimesResponse.class)))
    }, parameters = {
            @Parameter(name = "date", description = "조회하려는 날짜", required = true),
            @Parameter(name = "themeId", description = "조회하려는 테마 식별자", required = true)})
    abstract ResponseEntity<List<FindAvailableTimesResponse>> getAvailableTimes(LocalDate date, Long themeId);


    @Operation(summary = "방탈출 예약 검색 조회", description = "조건에 해당하는 예약 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "방탈출 검색 성공", content = @Content(schema = @Schema(implementation = FindReservationResponse.class)))
    }, parameters = {
            @Parameter(name = "themeId", description = "검색하려는 테마 식별자", required = true),
            @Parameter(name = "memberId", description = "검색하려는 회원 식별자", required = true),
            @Parameter(name = "dateFrom", description = "검색 시작 날짜", required = true),
            @Parameter(name = "dateTo", description = "검색 종료 날짜", required = true)})
    abstract ResponseEntity<List<FindReservationResponse>> searchBy(Long themeId, Long memberId, LocalDate dateFrom,
                                                                    LocalDate dateTo);

    @SecurityRequirement(name = "쿠키 인증 토큰")
    @Operation(summary = "방탈출 예약 삭제", description = "방탈출 예약을 삭제합니다. 대기가 존재하는 예약일 경우, 가장 먼저 등록된 대기가 자동으로 예약으로 승격됩니다.", responses = {
            @ApiResponse(responseCode = "204", description = "테마 삭제 성공"),
            @ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(name = "해당 테마에 대한 예약이 존재하는 경우", value = "식별자 1인 테마를 사용 중인 예약이 존재합니다. 삭제가 불가능합니다."))),
            @ApiResponse(responseCode = "403", content = @Content(examples = @ExampleObject(name = "해당 예약에 대한 회원의 권한이 존재하지 않는 경우, 즉 회원이 생성한 예약이 아닌 경우", value = "식별자 1인 예약에 대해 회원 식별자 1의 권한이 존재하지 않아, 삭제가 불가능합니다."))),
            @ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(name = "삭제하려는 예약이 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 예약이 존재하지 않습니다.")))}, 
            parameters = {@Parameter(name = "id", description = "삭제하려는 예약 식별자", required = true, example = "1")})
    abstract ResponseEntity<Void> cancelReservation(AuthInfo authInfo, Long id);

    @SecurityRequirement(name = "쿠키 인증 토큰")
    @Operation(summary = "방탈출 예약 목록 조회", description = "전체 예약 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "전체 예약 목록 조회 성공", content = @Content(schema = @Schema(implementation = FindReservationWithPaymentResponse.class)))
    })
    abstract ResponseEntity<List<FindReservationWithPaymentResponse>> getReservations(AuthInfo authInfo);
}
