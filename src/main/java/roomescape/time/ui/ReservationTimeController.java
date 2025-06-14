package roomescape.time.ui;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.uri.UriFactory;
import roomescape.time.application.ReservationTimeFacade;
import roomescape.time.ui.dto.CreateReservationTimeWebRequest;
import roomescape.time.ui.dto.ReservationTimeResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ReservationTimeController.BASE_PATH)
public class ReservationTimeController {

    public static final String BASE_PATH = "/times";

    private final ReservationTimeFacade reservationTimeFacade;

    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getAll() {
        log.info("[TIME] 전체 예약 시간 목록 조회 요청");
        final List<ReservationTimeResponse> reservationTimeResponses = reservationTimeFacade.getAll();
        return ResponseEntity.ok(reservationTimeResponses);
    }

    @PostMapping
    public ResponseEntity<ReservationTimeResponse> create(
            @RequestBody final CreateReservationTimeWebRequest createReservationTimeWebRequest) {
        log.info("[TIME] 예약 시간 생성 요청: {}", createReservationTimeWebRequest);
        final ReservationTimeResponse reservationTimeResponse = reservationTimeFacade.create(createReservationTimeWebRequest);
        final URI location = UriFactory.buildPath(BASE_PATH, String.valueOf(reservationTimeResponse.id()));
        return ResponseEntity.created(location)
                .body(reservationTimeResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        log.info("[TIME] 예약 시간 삭제 요청: id={}", id);
        reservationTimeFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
