package roomescape.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.WaitingService;
import roomescape.dto.LoginMember;
import roomescape.dto.request.WaitingRegisterDto;
import roomescape.dto.response.MemberWaitingResponseDto;
import roomescape.dto.response.WaitingResponseDto;

@Tag(name = "웨이팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/waiting")
public class WaitingController {

    private final WaitingService waitingService;

    @Operation(summary = "웨이팅 저장")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaitingResponseDto registerWaiting(@Parameter(hidden = true) LoginMember loginMember,
                                              @RequestBody @Valid WaitingRegisterDto waitingRegisterDto) {
        return waitingService.registerWaiting(loginMember, waitingRegisterDto);
    }

    @Operation(summary = "웨이팅 삭제")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaiting(LoginMember loginMember, @PathVariable Long id) {
        waitingService.deleteWaiting(loginMember, id);
    }

    @Operation(summary = "나의 웨이팅 전체 조회")
    @GetMapping("/mine")
    @ResponseStatus(HttpStatus.OK)
    public List<MemberWaitingResponseDto> getMyWaitings(LoginMember loginMember) {
        return waitingService.getMyWaitings(loginMember);
    }
}
