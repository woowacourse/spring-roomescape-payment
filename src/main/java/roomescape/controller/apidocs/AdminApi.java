package roomescape.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import roomescape.annotation.SecurityDocs;
import roomescape.dto.request.AdminCreateReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;

@SecurityDocs.Unauthorized
@SecurityDocs.Forbidden
@Tag(name = "[관리자 API]")
@SecurityRequirement(name = "cookieAuth")
public interface AdminApi {

    @Operation(summary = "예약 필터 조회", description = "회원ID, 테마ID, 날짜 범위로 예약을 필터링하여 조회합니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID",
                            schema = @Schema(type = "integer", format = "int64"),
                            required = false
                    ),
                    @Parameter(name = "themeId", description = "테마 ID",
                            schema = @Schema(type = "integer", format = "int64"),
                            required = false
                    ),
                    @Parameter(name = "dateFrom", description = "조회 시작 날짜 (yyyy-MM-dd)",
                            schema = @Schema(type = "string", format = "date"),
                            required = false
                    ),
                    @Parameter(name = "dateTo", description = "조회 종료 날짜 (yyyy-MM-dd)",
                            schema = @Schema(type = "string", format = "date"),
                            required = false
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "필터 대상 예약 데이터 조회 성공", value = """
                                    [
                                        {
                                            "id": 1,
                                            "name": "플린트",
                                            "time": "10:00:00",
                                            "date": "2025-06-04",
                                            "themeName": "공포의 방",
                                            "paymentKey": "paymentKey",
                                            "amount": 1000
                                        },
                                        {
                                            "id": 2,
                                            "name": "훌라",
                                            "time": "12:00:00",
                                            "date": "2025-06-03",
                                            "themeName": "추리의 방",
                                            "paymentKey": null,
                                            "amount": null
                                        }
                                    ]
                                    """)
                    }))
            }
    )
    ResponseEntity<List<ReservationResponse>> getReservationsByFilter(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo
    );

    @Operation(summary = "관리자 예약 생성", description = "관리자가 직접 예약을 생성합니다.",
            requestBody = @RequestBody(required = true, content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminCreateReservationRequest.class),
                    examples = @ExampleObject(value = """
                            {
                                "memberId": 1,
                                "date": "2025-06-05",
                                "timeId": 1,
                                "themeId": 1
                            }
                            """))))
    @ApiResponse(responseCode = "201", description = "예약 생성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationError")))
    ResponseEntity<ReservationResponse> createReservationByAdmin(AdminCreateReservationRequest request);

    @Operation(summary = "대기 예약 목록 조회", description = "예약 대기 중인 방탈출 예약 목록을 조회합니다. 관리자만 사용 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ReservationWaitResponse.class)),
                    examples = @ExampleObject(value = """
                            [
                                {
                                    "id": 1,
                                    "name": "플린트",
                                    "time": "10:00:00",
                                    "date": "2025-06-04",
                                    "theme": "공포의 방"
                                },
                                {
                                    "id": 2,
                                    "name": "훌라",
                                    "time": "12:00:00",
                                    "date": "2025-06-03",
                                    "theme": "추리의 방"
                                }
                            ]
                            """)
            )
    )
    ResponseEntity<List<ReservationWaitResponse>> getWaitReservations();

    @Operation(summary = "대기 예약 승인", description = "대기 상태인 예약을 승인합니다. 대기 상태를 '결제 대기'로 변경합니다.",
            parameters = {
                    @Parameter(name = "id", description = "예약 ID", required = true,
                            schema = @Schema(type = "integer", format = "int64"))
            }
    )
    @ApiResponse(responseCode = "200", description = "승인 성공")
    @ApiResponse(responseCode = "400", description = "대기가 아닌 예약", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "message": "대기 중인 예약이 아닙니다."
                    }
                    """
            )))
    @ApiResponse(responseCode = "400", description = "예약 정보 없음", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "message": "존재하지 않는 예약 대기입니다."
                    }
                    """
            )))
    ResponseEntity<Void> approveWaitReservation(@PathVariable("id") Long reservationId);

    @Operation(
            summary = "대기 예약 거절",
            description = "대기 상태인 예약을 거절합니다.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "예약 ID",
                            required = true,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiResponse(responseCode = "204", description = "거절 성공")
    @ApiResponse(responseCode = "400", description = "대기가 아닌 예약", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "message": "대기 중인 예약이 아닙니다."
                    }
                    """
            )))
    @ApiResponse(responseCode = "400", description = "예약 정보 없음", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "message": "존재하지 않는 예약 대기입니다."
                    }
                    """
            )))
    ResponseEntity<Void> rejectWaitReservation(@PathVariable("id") Long reservationId);
}