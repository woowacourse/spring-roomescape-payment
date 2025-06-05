package roomescape.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import roomescape.annotation.SecurityDocs;
import roomescape.dto.request.AvailableTimeRequest;
import roomescape.dto.request.CreateReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationTimeSlotResponse;

@Tag(name = "[예약 가능 시간 관리 API]")
@SecurityRequirement(name = "cookieAuth")
public interface ReservationTimeApi {

    @Operation(summary = "전체 예약 가능 시간 조회", description = "모든 예약 가능한 시간을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    [
                        {
                            "id": 1,
                            "time": "10:00:00"
                        },
                        {
                            "id": 2,
                            "time": "12:00:00"
                        }
                    ]
                    """)
    ))
    ResponseEntity<List<ReservationTimeResponse>> getReservationTimes();

    @Operation(summary = "특정 날짜의 예약 가능한 시간대 조회", description = "특정 날짜와 테마에 대해 예약 가능한 시간대를 조회합니다.",
            parameters = {
                    @Parameter(name = "date", description = "조회할 날짜 (yyyy-MM-dd)", required = true,
                            schema = @Schema(type = "string", format = "date")
                    ),
                    @Parameter(name = "themeId", description = "테마 ID", required = true,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    [
                        {
                            "timeId": 1,
                            "time": "10:00:00",
                            "isAvailable": true
                        },
                        {
                            "timeId": 2,
                            "time": "12:00:00",
                            "isAvailable": false
                        }
                    ]
                    """)))
    ResponseEntity<List<ReservationTimeSlotResponse>> getAvailableReservationTimes(
            @Parameter(hidden = true) AvailableTimeRequest request);

    @SecurityDocs.Unauthorized
    @SecurityDocs.Forbidden
    @Operation(summary = "예약 가능 시간 추가", description = "새로운 예약 가능 시간을 추가합니다. 관리자만 사용 가능합니다.",
            requestBody = @RequestBody(required = true, content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreateReservationTimeRequest.class),
                    examples = @ExampleObject(value = """
                            {
                                "startAt": "14:00:00"
                            }
                            """)
            )
            )
    )
    @ApiResponse(responseCode = "201", description = "시간 추가 성공", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "id": 3,
                        "time": "14:00:00"
                    }
                    """)))
    @ApiResponse(responseCode = "400", description = "중복된 시간", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "message": "이미 존재하는 예약 시간입니다."
                    }
                    """)))
    ResponseEntity<ReservationTimeResponse> addReservationTime(
            @RequestBody CreateReservationTimeRequest request
    );

    @SecurityDocs.Unauthorized
    @SecurityDocs.Forbidden
    @Operation(summary = "예약 가능 시간 삭제", description = "예약 가능 시간을 삭제합니다. 관리자만 사용 가능합니다.",
            parameters = {
                    @Parameter(name = "id", description = "삭제할 시간 ID", required = true,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "400", description = "존재하지 않는 시간",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "존재하지 않는 예약 시간입니다."
                    }
                    """)
            )
    )
    ResponseEntity<Void> deleteReservationTime(@PathVariable Long id);
}
