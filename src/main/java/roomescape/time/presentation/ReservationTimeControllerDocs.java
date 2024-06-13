package roomescape.time.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.ReservationTimeAddRequest;
import roomescape.time.dto.ReservationTimeResponse;

@Tag(name = "예약 시간", description = "예약 시간 API")
public interface ReservationTimeControllerDocs {

    @Operation(summary = "예약 시간 목록 조회", description = "예약 시간 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 시간 목록 조회 성공")
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimeList();

    @Operation(summary = "예약 시간 추가", description = "예약 시간을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 시간 추가 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 예약시간인 경우")
    })
    public ResponseEntity<ReservationTimeResponse> saveReservationTime(ReservationTimeAddRequest request);

    @Operation(summary = "예약 가능 시간 조회", description = "예약 가능 시간을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 가능 시간 조회 성공")
    public ResponseEntity<List<AvailableTimeResponse>> readTimesStatus(LocalDate date, Long themeId);

    @Operation(summary = "예약 시간 삭제", description = "예약 시간을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "예약 시간 삭제 성공")
    public ResponseEntity<Void> removeReservationTime(Long id);
}
