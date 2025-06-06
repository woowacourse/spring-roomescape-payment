package roomescape.controller.docs.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;

@Tag(name = "예약 테마 관리", description = "방탈출 테마 조회, 생성, 삭제 및 예약 가능 시간 조회 API")
public interface ReservationThemeControllerDocs {

    @Operation(
            summary = "전체 테마 목록 조회",
            description = "시스템에 등록된 모든 방탈출 테마를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "테마 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationThemeResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 1,
                            "name": "공포의 저택",
                            "description": "무서운 공포 테마로 짜릿한 스릴을 경험하세요",
                            "thumbnail": "https://example.com/horror-house.jpg"
                        },
                        {
                            "id": 2,
                            "name": "미스터리 하우스",
                            "description": "추리와 논리로 풀어나가는 미스터리 테마",
                            "thumbnail": "https://example.com/mystery-house.jpg"
                        }
                    ]
                    """
                            )
                    )
            )
    })
    ResponseEntity<List<ReservationThemeResponse>> reservationThemeList();

    @Operation(
            summary = "인기 테마 랭킹 조회",
            description = """
        지난 7일간(어제~7일전) 예약 건수를 기준으로 한 인기 테마 순위를 조회합니다.
        최대 10개의 테마가 예약 건수 내림차순으로 반환됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인기 테마 랭킹 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationThemeResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 2,
                            "name": "미스터리 하우스",
                            "description": "추리와 논리로 풀어나가는 미스터리 테마",
                            "thumbnail": "https://example.com/mystery-house.jpg"
                        },
                        {
                            "id": 1,
                            "name": "공포의 저택",
                            "description": "무서운 공포 테마로 짜릿한 스릴을 경험하세요",
                            "thumbnail": "https://example.com/horror-house.jpg"
                        }
                    ]
                    """
                            )
                    )
            )
    })
    ResponseEntity<List<ReservationThemeResponse>> reservationThemeRankingList();

    @Operation(
            summary = "테마 생성",
            description = """
        새로운 방탈출 테마를 생성합니다.
        1. 테마 이름의 중복을 검증합니다.
        2. 이름, 설명, 썸네일 이미지를 포함하여 저장합니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "테마 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationThemeResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 3,
                        "name": "스파이 미션",
                        "description": "첩보원이 되어 임무를 수행하는 액션 테마",
                        "thumbnail": "https://example.com/spy-mission.jpg"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 존재하는 테마 이름",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "테마 이름 중복",
                                    value = "{\"errorMessage\": \"이미 존재하는 테마입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<ReservationThemeResponse> reservationThemeAdd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "테마 생성 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationThemeRequest.class),
                            examples = @ExampleObject(
                                    name = "테마 생성 요청 예시",
                                    summary = "새로운 테마 생성 요청",
                                    value = """
                    {
                        "name": "스파이 미션",
                        "description": "첩보원이 되어 임무를 수행하는 액션 테마",
                        "thumbnail": "https://example.com/spy-mission.jpg"
                    }
                    """
                            )
                    )
            )
            @RequestBody ReservationThemeRequest request
    );

    @Operation(
            summary = "테마 삭제",
            description = """
        특정 테마를 삭제합니다.
        해당 테마에 예약이 존재하는 경우 데이터 무결성을 위해 삭제가 거부됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "테마 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "예약이 존재하여 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "삭제 불가",
                                    value = "{\"errorMessage\": \"예약이 존재해 테마를 삭제할 수 없습니다.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 테마",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "테마 조회 실패",
                                    value = "{\"errorMessage\": \"존재하지 않는 테마입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<Void> reservationThemeRemove(
            @Parameter(
                    description = "삭제할 테마의 ID",
                    required = true,
                    example = "3"
            )
            @PathVariable(name = "id") long id
    );

    @Operation(
            summary = "특정 테마의 예약 가능 시간 조회",
            description = """
        특정 테마와 날짜에 대한 모든 예약 시간의 예약 가능 여부를 조회합니다.
        - 각 시간대별로 예약 가능(false) 또는 예약됨(true) 상태를 반환합니다.
        - 모든 등록된 예약 시간이 조회되며, 예약 여부가 함께 표시됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 가능 시간 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationTimeWithAvailabilityResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 1,
                            "startAt": "10:00:00",
                            "isBooked": false
                        },
                        {
                            "id": 2,
                            "startAt": "12:00:00",
                            "isBooked": true
                        },
                        {
                            "id": 3,
                            "startAt": "14:00:00",
                            "isBooked": false
                        },
                        {
                            "id": 4,
                            "startAt": "16:00:00",
                            "isBooked": true
                        }
                    ]
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 테마",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "테마 조회 실패",
                                    value = "{\"errorMessage\": \"존재하지 않는 테마입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<List<ReservationTimeWithAvailabilityResponse>> reservationTimeOfTheme(
            @Parameter(
                    description = "조회할 테마의 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @Parameter(
                    description = "조회할 날짜 (YYYY-MM-DD 형식)",
                    required = true,
                    example = "2024-12-25"
            )
            @RequestParam LocalDate date
    );
}
