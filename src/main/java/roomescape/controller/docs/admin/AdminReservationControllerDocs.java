package roomescape.controller.docs.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.WaitingReservationResponse;

@Tag(name = "관리자 예약 관리", description = "관리자 전용 예약 관리 API - 모든 사용자의 예약을 관리할 수 있습니다")
public interface AdminReservationControllerDocs {

    @Operation(
            summary = "예약 생성",
            description = "관리자가 새로운 예약을 직접 생성합니다. 생성된 예약은 즉시 승인 상태(ACCEPTED)가 됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "예약이 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
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
            @RequestBody(
                    description = "예약 생성 요청 데이터",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                    {
                        "memberId": 1,
                        "themeId": 2,
                        "timeId": 3,
                        "date": "2024-12-25"
                    }
                    """
                            )
                    )
            )
            CreateReservationRequest request
    );

    @Operation(
            summary = "예약 목록 조회 (필터링)",
            description = """
        관리자가 필터 조건에 따라 모든 사용자의 예약을 조회합니다.
        모든 필터 조건은 선택사항이며, 조건을 조합하여 사용할 수 있습니다.
        - memberId: 특정 회원의 예약만 조회
        - themeId: 특정 테마의 예약만 조회  
        - dateFrom: 해당 날짜 이후의 예약만 조회
        - dateTo: 해당 날짜 이전의 예약만 조회
        """
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
                                "startAt": "10:00:00"
                            },
                            "theme": {
                                "id": 2,
                                "name": "공포의 저택",
                                "description": "무서운 공포 테마",
                                "thumbnail": "https://example.com/horror-house.jpg"
                            },
                            "status": "승인됨"
                        }
                    ]
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 필터 조건 (예: 잘못된 날짜 형식)",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<List<ReservationResponse>> getReservationsByFilter(
            @Parameter(description = "회원 ID로 필터링 (선택사항)", example = "1")
            @RequestParam(required = false, name = "memberId") Long memberId,
            @Parameter(description = "테마 ID로 필터링 (선택사항)", example = "2")
            @RequestParam(required = false, name = "themeId") Long themeId,
            @Parameter(description = "시작 날짜로 필터링 (해당 날짜 이후 예약 조회, 선택사항)", example = "2024-01-01")
            @RequestParam(required = false, name = "dateFrom") LocalDate dateFrom,
            @Parameter(description = "종료 날짜로 필터링 (해당 날짜 이전 예약 조회, 선택사항)", example = "2024-12-31")
            @RequestParam(required = false, name = "dateTo") LocalDate dateTo
    );

    @Operation(
            summary = "대기 예약 목록 조회",
            description = """
        관리자가 모든 대기 중인 예약(PENDING 상태)을 조회합니다.
        대기 예약은 이미 예약된 시간대에 추가로 예약을 요청한 경우 생성됩니다.
        최근 등록된 순서로 정렬되어 반환됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "대기 예약 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WaitingReservationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 5,
                            "member": {
                                "memberId": 3,
                                "name": "김철수"
                            },
                            "theme": {
                                "themeId": 1,
                                "themeName": "미스터리 하우스"
                            },
                            "time": {
                                "timeId": 2,
                                "startAt": "14:00:00"
                            },
                            "date": "2024-12-25"
                        }
                    ]
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<List<WaitingReservationResponse>> getWaitingReservations();

    @Operation(
            summary = "대기 예약 거부",
            description = """
        관리자가 특정 대기 예약을 거부합니다.
        거부된 예약의 상태는 DENIED로 변경되며, 해당 예약은 더 이상 처리되지 않습니다.
        오직 PENDING 상태의 예약만 거부할 수 있습니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "대기 예약 거부 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "대기 상태가 아닌 예약을 거부하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"errorMessage\": \"대기 상태의 예약만 거절할 수 있습니다.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 예약 ID",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"errorMessage\": \"존재하지 않는 예약입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<Void> denyWaitingReservation(
            @Parameter(description = "거부할 대기 예약의 ID", required = true, example = "5")
            @PathVariable Long reservationId
    );
}
