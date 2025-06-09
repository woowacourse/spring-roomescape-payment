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
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.controller.apidocs.annotation.SecurityDocs;
import roomescape.dto.request.ConfirmWaitReservationRequest;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.CreateWaitReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.response.MyReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;
import roomescape.exception.dto.ErrorResponse;

@Tag(name = "[방탈출 예약 API]")
@SecurityRequirement(name = "cookieAuth")
public interface ReservationController {

    @SecurityDocs.Unauthorized
    @SecurityDocs.Forbidden
    @Operation(summary = "전체 예약 데이터 조회", description = "'예약'된 방탈출 예약 전체를 조회합니다. 관리자만 사용 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "전체 예약 데이터 조회 성공", value = """
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
    ResponseEntity<List<ReservationResponse>> getReservations();

    @SecurityDocs.Unauthorized
    @Operation(summary = "사용자 전체 예약 데이터 조회", description = "사용자가 예약한 방탈출 예약 전체를 조회합니다. 인증된 유저 및 관리자만 사용 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "사용자의 전체 예약 데이터 조회 성공", value = """
                    [
                      {
                        "id": 1,
                        "theme": "추리의 방",
                        "date": "2025-06-02",
                        "time": "14:00:00",
                        "status": "예약",
                        "paymentKey": "paymentKeyId",
                        "amount": 1000
                      },
                      {
                        "id": 2,
                        "theme": "공포의 방",
                        "date": "2025-07-01",
                        "time": "10:00:00",
                        "status": "2 번째 예약대기",
                        "paymentKey": null,
                        "amount": null
                      },
                      {
                        "id": 3,
                        "theme": "추리의 방",
                        "date": "2025-07-03",
                        "time": "10:00:00",
                        "status": "결제 대기",
                        "paymentKey": null,
                        "amount": null
                      }
                    ]
                    """)
    }))
    ResponseEntity<List<MyReservationResponse>> getMyReservation(
            @Parameter(hidden = true) LoginMemberRequest loginMemberRequest);

    @SecurityDocs.Unauthorized
    @Operation(summary = "방탈출 예약 추가", description = "방탈출 예약을 추가합니다. 인증된 유저 및 관리자만 사용 가능합니다.",
            requestBody = @RequestBody(required = true, content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreateReservationRequest.class),
                    examples = @ExampleObject(name = "방탈출 예약 추가 요청 예시", value = """
                            {
                                "date": "3000-07-25",
                                "timeId": 1,
                                "themeId": 1,
                                "paymentKey": "paymentKeyId",
                                "orderId": "orderIdValue",
                                "amount": 1000,
                                "paymentType": "NORMAL"
                            }
                            """))))
    @ApiResponse(responseCode = "201", description = "추가 성공", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "방탈출 예약 추가 성공", value = """
                    {
                        "id": 1,
                        "name": "플린트",
                        "time": "10:00:00",
                        "date": "3000-07-25",
                        "themeName": "공포의 방",
                        "paymentKey": "paymentKeyId",
                        "amount": 1000
                    }
                    """)
    }))
    @ApiResponse(responseCode = "400", description = "추가 실패", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "존재하지 않는 멤버 ID로 시도", value = """
                    {
                      "message": "존재하지 않는 멤버 ID입니다."
                    }
                    """),
            @ExampleObject(name = "존재하지 않는 예약 시간으로 추가 시도", value = """
                    {
                      "message": "존재하지 않는 예약 시간입니다."
                    }
                    """),
            @ExampleObject(name = "존재하지 않는 테마로 추가 시도", value = """
                    {
                      "message": "존재하지 않는 테마입니다."
                    }
                    """),
            @ExampleObject(name = "유저가 예약한 같은 날짜, 시간, 테마인 예약이 존재", value = """
                    {
                      "message": "이미 예약이 존재합니다."
                    }
                    """),
            @ExampleObject(name = "과거 날짜 및 시간으로 예약 시도", value = """
                    {
                      "message": "과거 날짜 및 시간으로 예약할 수 없습니다."
                    }
                    """)
    }))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)),
            examples = @ExampleObject(name = "입력값 검증 실패",
                    description = "입력값 존재 여부 또는 유효하지 않은 값일 경우 제공되는 오류 메시지입니다.", value = """
                    [
                        {
                            "message": "시간은 비어있을 수 없습니다."
                        }
                    ]
                    """)))
    ResponseEntity<ReservationResponse> addReservations(CreateReservationRequest request,
                                                        @Parameter(hidden = true) LoginMemberRequest loginMemberRequest);

    @SecurityDocs.Unauthorized
    @Operation(summary = "방탈출 예약 대기 추가", description = "방탈출 예약 대기를 추가합니다. 인증된 유저 및 관리자만 사용 가능합니다.",
            requestBody = @RequestBody(required = true, content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreateWaitReservationRequest.class),
                    examples = @ExampleObject(name = "방탈출 예약 대기 추가 요청 예시", value = """
                            {
                                "date": "3000-07-25",
                                "timeId": 1,
                                "themeId": 1
                            }
                            """))))
    @ApiResponse(responseCode = "201", description = "추가 성공", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "방탈출 예약 대기 추가 성공", value = """
                    {
                        "date": "3000-07-25",
                        "timeId": 1,
                        "themeId": 1
                    }
                    """)
    }))
    @ApiResponse(responseCode = "400", description = "추가 실패", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "존재하지 않는 멤버 ID로 시도", value = """
                    {
                      "message": "존재하지 않는 멤버 ID입니다."
                    }
                    """),
            @ExampleObject(name = "존재하지 않는 예약 시간으로 추가 시도", value = """
                    {
                      "message": "존재하지 않는 예약 시간입니다."
                    }
                    """),
            @ExampleObject(name = "존재하지 않는 테마로 추가 시도", value = """
                    {
                      "message": "존재하지 않는 테마입니다."
                    }
                    """),
            @ExampleObject(name = "유저가 예약한 같은 날짜, 시간, 테마인 예약이 존재", value = """
                    {
                      "message": "이미 예약이 존재합니다."
                    }
                    """),
            @ExampleObject(name = "과거 날짜 및 시간으로 예약 시도", value = """
                    {
                      "message": "과거 날짜 및 시간으로 예약할 수 없습니다."
                    }
                    """)
    }))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)),
            examples = @ExampleObject(name = "입력값 검증 실패",
                    description = "입력값 존재 여부 또는 유효하지 않은 값일 경우 제공되는 오류 메시지입니다.", value = """
                    [
                        {
                            "message": "시간은 비어있을 수 없습니다."
                        }
                    ]
                    """)))
    ResponseEntity<ReservationWaitResponse> addWaitReservation(CreateWaitReservationRequest request,
                                                               @Parameter(hidden = true) LoginMemberRequest loginMemberRequest);

    @SecurityDocs.Unauthorized
    @Operation(summary = "방탈출 예약 확정", description = "결제 대기 중인 방탈출 예약을 확정합니다. 인증된 유저 및 관리자만 사용 가능합니다.",
            requestBody = @RequestBody(required = true, content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreateReservationRequest.class),
                    examples = @ExampleObject(name = "방탈출 예약 추가 요청 예시", value = """
                            {
                                        "name": "apitest",
                                        "email": "apitest@apitest.com",
                                        "password": "apitest",
                                        "paymentKey": "paymentKeyId",
                                        "orderId": "orderId",
                                        "amount": 1000,
                                        "paymentType": "NORMAL"
                                     }
                            """))))
    @ApiResponse(responseCode = "200", description = "확정 성공", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "방탈출 예약 확정 성공", value = """
                    {
                      "paymentKey": "paymentKeyId",
                      "orderId": "orderId",
                      "amount": 1000,
                      "paymentType": "NORMAL"
                    }
                    """)
    }))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)),
            examples = @ExampleObject(name = "입력값 검증 실패",
                    description = "입력값 존재 여부 또는 유효하지 않은 값일 경우 제공되는 오류 메시지입니다.", value = """
                    [
                        {
                            "message": "payment key 는 필수입니다."
                        },
                        {
                            "message": "order ID 는 필수입니다."
                        },
                        {
                            "message": "금액은 음수가 될 수 없습니다."
                        },
                        {
                            "message": "결제 유형은 필수입니다."
                        }
                    ]
                    """)))
    ResponseEntity<ReservationResponse> confirmWaitReservation(Long reservationId,
                                                               ConfirmWaitReservationRequest request,
                                                               @Parameter(hidden = true) LoginMemberRequest loginMemberRequest);

    @SecurityDocs.Unauthorized
    @Operation(summary = "방탈출 예약 취소", description = "방탈출 예약을 취소합니다. 인증된 유저 및 관리자만 사용 가능합니다.")
    @ApiResponse(responseCode = "200", description = "취소 성공", content = @Content(mediaType = "application/json",
            examples = {
                    @ExampleObject(name = "방탈출 예약 취소 성공")
            }))
    @ApiResponse(responseCode = "400", description = "취소 실패", content = @Content(mediaType = "application/json",
            examples = {
                    @ExampleObject(name = "존재하지 않는 예약", value = """
                            {
                                "message": "존재하지 않는 예약 입니다."
                            }
                            """)
            }))
    ResponseEntity<Void> deleteReservations(Long id);

    @SecurityDocs.Unauthorized
    @Operation(summary = "방탈출 예약 대기 취소", description = "방탈출 예약 대기를 취소합니다. 인증된 유저 및 관리자만 사용 가능합니다.")
    @ApiResponse(responseCode = "200", description = "취소 성공", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "취소 실패", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "존재하지 않는 예약", value = """
                    {
                        "message": "존재하지 않는 예약 입니다."
                    }
                    """)
    }))
    ResponseEntity<Void> deleteWaitReservation(Long id);
}
