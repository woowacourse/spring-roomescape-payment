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
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;

@Tag(name = "예약 시간 관리", description = "예약 가능 시간 조회, 생성, 삭제 API")
public interface ReservationTimeControllerDocs {

    @Operation(
            summary = "전체 예약 시간 목록 조회",
            description = "시스템에 등록된 모든 예약 가능 시간을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 시간 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationTimeResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 1,
                            "startAt": "10:00:00"
                        },
                        {
                            "id": 2,
                            "startAt": "12:00:00"
                        },
                        {
                            "id": 3,
                            "startAt": "14:00:00"
                        },
                        {
                            "id": 4,
                            "startAt": "16:00:00"
                        },
                        {
                            "id": 5,
                            "startAt": "18:00:00"
                        }
                    ]
                    """
                            )
                    )
            )
    })
    ResponseEntity<List<ReservationTimeResponse>> reservationTimeList();

    @Operation(
            summary = "예약 시간 생성",
            description = """
        새로운 예약 시간을 생성합니다.
        1. 시간 중복을 검증합니다 (동일한 시작 시간이 이미 존재하는지 확인)
        2. 유효성 검증을 통과한 시간을 저장합니다.
        
        시간 형식: HH:mm (예: 14:30)
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "예약 시간 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationTimeResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 6,
                        "startAt": "20:00:00"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 존재하는 예약 시간",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "시간 중복",
                                    value = "{\"errorMessage\": \"이미 존재하는 예약 시간 입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<ReservationTimeResponse> reservationTimeAdd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예약 시간 생성 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationTimeRequest.class),
                            examples = @ExampleObject(
                                    name = "예약 시간 생성 요청 예시",
                                    summary = "새로운 예약 시간 생성 요청",
                                    value = """
                    {
                        "startAt": "20:00"
                    }
                    """
                            )
                    )
            )
            @RequestBody @Valid ReservationTimeRequest request
    );

    @Operation(
            summary = "예약 시간 삭제",
            description = """
        특정 예약 시간을 삭제합니다.
        해당 시간에 예약이 존재하는 경우 데이터 무결성을 위해 삭제가 거부됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "예약 시간 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "예약이 존재하여 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "삭제 불가",
                                    value = "{\"errorMessage\": \"이미 예약이 존재해 시간을 삭제할 수 없습니다.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 예약 시간",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "시간 조회 실패",
                                    value = "{\"errorMessage\": \"존재하는 시간이 없습니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<Void> reservationTimeRemove(
            @Parameter(
                    description = "삭제할 예약 시간의 ID",
                    required = true,
                    example = "6"
            )
            @PathVariable(name = "id") long id
    );
}
