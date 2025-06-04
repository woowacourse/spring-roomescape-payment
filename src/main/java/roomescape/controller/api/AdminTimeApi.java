package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;

import java.util.List;

@Tag(name = "4. 예약 시간 관련 API")
public interface AdminTimeApi {

    @Operation(summary = "예약 시간 추가")
    ResponseEntity<ReservationTimeResponse> save(
            @RequestBody(required = true) ReservationTimeRequest request
    );

    @Operation(summary = "모든 예약 시간 조회")
    ResponseEntity<List<ReservationTimeResponse>> getAll();

    @Operation(summary = "예약 시간 삭제")
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long timeId
    );
}
