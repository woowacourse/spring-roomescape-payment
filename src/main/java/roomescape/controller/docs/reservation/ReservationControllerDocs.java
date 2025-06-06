package roomescape.controller.docs.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.ReservationResponse;

@Tag(name = "예약 관리", description = "일반 사용자 예약 관리 API - 예약 조회, 생성, 대기 예약, 삭제")
public interface ReservationControllerDocs {

    @Operation(
            summary = "전체 예약 목록 조회",
            description = "시스템에 등록된 모든 예약을 조회합니다. 관리자 및 일반 사용자 모두 사용 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 1,
                            "name": "홍길동",
                            "date": "2024-12-25",
                            "time": {
                                "id": 3,
                                "startAt": "14:00:00"
                            },
                            "theme": {
                                "id": 2,
                                "name": "공포의 저택",
                                "description": "무서운 공포 테마",
                                "thumbnail": "https://example.com/horror-house.jpg"
                            },
                            "status": "ACCEPTED"
                        },
                        {
                            "id": 2,
                            "name": "홍길순",
                            "date": "2024-12-25",
                            "time": {
                                "id": 5,
                                "startAt": "11:00:00"
                            },
                            "theme": {
                                "id": 2,
                                "name": "공포의 저택",
                                "description": "무서운 공포 테마",
                                "thumbnail": "https://example.com/horror-house.jpg"
                            },
                            "status": "ACCEPTED"
                        }
                    ]
                    """
                            )
                    )
            )
    })
    ResponseEntity<List<ReservationResponse>> reservationList();

    @Operation(
            summary = "예약 생성 (결제 포함)",
            description = """
        결제와 함께 새로운 예약을 생성합니다.
        1. 예약 가능성을 검증합니다 (이미 예약된 시간이면 실패).
        2. 중복 예약을 검증합니다 (같은 사용자가 동일한 예약 항목에 예약하면 실패).
        3. 예약을 생성합니다 (즉시 ACCEPTED 상태).
        4. 토스 페이먼츠로 결제를 승인합니다.
        5. 결제 정보를 저장합니다.
        
        트랜잭션으로 처리되므로 결제 실패 시 예약도 롤백됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "예약 및 결제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 1,
                        "name": "홍길동",
                        "date": "2024-12-25",
                        "time": {
                            "id": 3,
                            "startAt": "14:00:00"
                        },
                        "theme": {
                            "id": 2,
                            "name": "공포의 저택",
                            "description": "무서운 공포 테마",
                            "thumbnail": "https://example.com/horror-house.jpg"
                        },
                        "status": "ACCEPTED"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "예약 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "이미 예약된 시간",
                                            value = "{\"errorMessage\": \"이미 예약된 시간입니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "중복 예약",
                                            value = "{\"errorMessage\": \"이미 예약을 등록하였습니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "결제 실패",
                                            value = "{\"errorMessage\": \"결제 처리 중 오류가 발생했습니다.\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 리소스",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 사용자",
                                            value = "{\"errorMessage\": \"존재하지 않는 사용자입니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 테마",
                                            value = "{\"errorMessage\": \"존재하지 않는 테마입니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 시간",
                                            value = "{\"errorMessage\": \"존재하는 시간이 없습니다.\"}"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<ReservationResponse> addReservation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예약 생성 요청 데이터 (결제 정보 포함)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationRequest.class),
                            examples = @ExampleObject(
                                    name = "결제 포함 예약 요청 예시",
                                    summary = "토스 페이먼츠 결제 정보가 포함된 예약 요청",
                                    value = """
                    {
                        "date": "2024-12-25",
                        "themeId": 2,
                        "timeId": 3,
                        "paymentKey": "test_payment_key_12345",
                        "orderId": "order_20241225_001",
                        "amount": 30000,
                        "paymentType": "CARD"
                    }
                    """
                            )
                    )
            )
            @RequestBody @Valid ReservationRequest request,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "대기 예약 생성",
            description = """
        이미 예약된 시간대에 대기 예약을 생성합니다.
        1. 기존 예약이 존재하는지 검증합니다 (없으면 실패).
        2. 중복 예약을 검증합니다 (같은 사용자가 동일한 예약 항목에 예약하면 실패).
        3. PENDING 상태로 예약을 생성합니다.
        
        대기 예약은 결제 없이 생성되며, 승인된 예약이 취소될 때 자동으로 승인됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "대기 예약 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 5,
                        "name": "김철수",
                        "date": "2024-12-25",
                        "time": {
                            "id": 3,
                            "startAt": "14:00:00"
                        },
                        "theme": {
                            "id": 2,
                            "name": "공포의 저택",
                            "description": "무서운 공포 테마",
                            "thumbnail": "https://example.com/horror-house.jpg"
                        },
                        "status": "대기중"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "대기 예약 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "기존 예약 없음",
                                            value = "{\"errorMessage\": \"대기 예약은 기존 예약이 있을 때만 가능합니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "중복 예약",
                                            value = "{\"errorMessage\": \"이미 예약을 등록하였습니다.\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 리소스",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 사용자",
                                            value = "{\"errorMessage\": \"존재하지 않는 사용자입니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 테마",
                                            value = "{\"errorMessage\": \"존재하지 않는 테마입니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 시간",
                                            value = "{\"errorMessage\": \"존재하는 시간이 없습니다.\"}"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<ReservationResponse> addPendingReservation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "대기 예약 생성 요청 데이터 (결제 정보 제외)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationRequest.class),
                            examples = @ExampleObject(
                                    name = "대기 예약 요청 예시",
                                    summary = "결제 정보는 무시되고 날짜, 테마, 시간만 사용",
                                    value = """
                    {
                        "date": "2024-12-25",
                        "themeId": 2,
                        "timeId": 3
                    }
                    """
                            )
                    )
            )
            @RequestBody @Valid ReservationRequest request,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "예약 삭제",
            description = """
        예약을 삭제합니다. 예약 상태에 따라 다르게 처리됩니다:
        
        **PENDING 상태**: 단순히 예약만 삭제합니다.
        **ACCEPTED 상태**: 
        1. 같은 예약 항목에 대기 중인 예약이 있으면 가장 먼저 등록된 대기 예약을 승인합니다.
        2. 대기 예약이 없으면 예약과 함께 예약 항목도 삭제합니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "예약 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 예약",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "예약 조회 실패",
                                    value = "{\"errorMessage\": \"존재하지 않는 예약입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<Void> removeReservation(
            @Parameter(description = "삭제할 예약의 ID", required = true, example = "1")
            @PathVariable(name = "id") long id
    );
}
