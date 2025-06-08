package roomescape.time.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.time.controller.dto.CreateReservationTimeWebRequest;
import roomescape.time.controller.dto.ReservationTimeWebResponse;

public interface ReservationTimeController {

    @Tag(name = "예약 시간 API")
    @Operation(summary = "예약 시간 목록 조회", security = @SecurityRequirement(name = "loginAuth"))
    List<ReservationTimeWebResponse> getAll();

    @Tag(name = "예약 시간 API [관리자 권한]")
    @Operation(summary = "예약 시간 생성", security = @SecurityRequirement(name = "loginAuth"))
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = CreateReservationTimeWebRequest.class),
                    examples = {
                            @ExampleObject(name = "sample", value = "{\"startAt\":\"16:00\"}")
                    }
            )
    )
    ResponseEntity<ReservationTimeWebResponse> create(CreateReservationTimeWebRequest createReservationTimeWebRequest);

    @Tag(name = "예약 시간 API [관리자 권한]")
    @Operation(summary = "예약 시간 삭제", security = @SecurityRequirement(name = "loginAuth"))
    ResponseEntity<Void> delete(Long id);
}
