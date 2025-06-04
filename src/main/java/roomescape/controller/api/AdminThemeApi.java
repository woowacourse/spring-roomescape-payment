package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;

@Tag(name = "5. 어드민 전용 API")
public interface AdminThemeApi {

    @Operation(summary = "테마 추가")
    ResponseEntity<ReservationThemeResponse> save(
            ReservationThemeRequest request
    );

    @Operation(summary = "테마 삭제")
    ResponseEntity<Void> remove(
            long themeId
    );
}
