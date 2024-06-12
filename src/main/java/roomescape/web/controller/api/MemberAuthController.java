package roomescape.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.infrastructure.auth.JwtProvider;
import roomescape.service.MemberAuthService;
import roomescape.service.request.MemberSignUpAppRequest;
import roomescape.service.response.MemberAppResponse;
import roomescape.web.controller.request.MemberSignUpRequest;
import roomescape.web.controller.request.TokenRequest;
import roomescape.web.controller.response.MemberResponse;

@Tag(name = "Auth", description = "인증 API")
@RestController
public class MemberAuthController {

    private final MemberAuthService memberAuthService;
    private final JwtProvider jwtProvider;

    public MemberAuthController(MemberAuthService memberAuthService, JwtProvider jwtProvider) {
        this.memberAuthService = memberAuthService;
        this.jwtProvider = jwtProvider;
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 토큰 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody TokenRequest request,
                                      HttpServletResponse response) {
        if (memberAuthService.isExistsMemberByEmailAndPassword(request.email(), request.password())) {
            String token = jwtProvider.createToken(request.email());
            response.addCookie(memberAuthService.createCookieByToken(token));
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 확인", description = "요청 헤더에 담긴 토큰으로 권한을 인증합니다.")
    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> findMember(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new RoomescapeException(RoomescapeErrorCode.UNAUTHORIZED, "로그인한 회원만 접근 가능합니다.");
        }
        String token = memberAuthService.extractTokenFromCookies(request.getCookies());
        String email = jwtProvider.getPayload(token);
        MemberAppResponse appResponse = memberAuthService.findMemberByEmail(email);
        MemberResponse response = new MemberResponse(appResponse.id(), appResponse.name(), appResponse.role());

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "회원가입", description = "이름, 이메일, 비밀번호로 회원가입을 요청합니다.")
    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        MemberAppResponse appResponse = memberAuthService.signUp(
                new MemberSignUpAppRequest(request.name(), request.email(), request.password()));

        MemberResponse response = new MemberResponse(appResponse.id(), appResponse.name(), appResponse.role());
        return ResponseEntity.created(URI.create("/member" + appResponse.id())).body(response);
    }

    @Operation(summary = "로그아웃", description = "요청 헤더에 담긴 토큰으로 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = memberAuthService.extractTokenFromCookies(request.getCookies());
        String expiredToken = jwtProvider.createExpiredToken(token);
        response.addCookie(memberAuthService.createCookieByToken(expiredToken));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 회원 조회", description = "전체 회원 목록을 조회합니다.")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getMembers() {
        List<MemberAppResponse> appResponses = memberAuthService.findAll();

        List<MemberResponse> responses = appResponses.stream()
                .map(MemberResponse::from)
                .toList();

        return ResponseEntity.ok().body(responses);
    }
}
