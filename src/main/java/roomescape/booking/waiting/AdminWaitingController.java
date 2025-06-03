package roomescape.booking.waiting;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.booking.waiting.dto.WaitingResponse;

import java.util.List;

@RestController
@RequestMapping("/admin/waitings")
@RequiredArgsConstructor
public class AdminWaitingController {

    private final WaitingService waitingService;

    @GetMapping
    public ResponseEntity<List<WaitingResponse>> readAll() {
        List<WaitingResponse> waitingResponses = waitingService.readAll();
        return ResponseEntity.ok(waitingResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") final Long id
    ) {
        waitingService.deleteByIdForAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
