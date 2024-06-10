package roomescape.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.LoginMember;
import roomescape.dto.request.TokenRequest;
import roomescape.dto.response.MemberResponse;
import roomescape.dto.response.TokenResponse;
import roomescape.service.CookieService;
import roomescape.service.MemberService;

@Tag(name = "Login", description = "Operations related to logins")
@RestController
public class LoginController {

    private final CookieService cookieService;
    private final MemberService memberService;

    public LoginController(CookieService cookieService, MemberService memberService) {
        this.cookieService = cookieService;
        this.memberService = memberService;
    }

    @Operation(summary = "Login",
            description = "This endpoint helps everyone to login the service",
            tags = {"Login API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Parameter(description = "Token request payload", required = true)
            @RequestBody TokenRequest tokenRequest) {
        TokenResponse tokenResponse = memberService.createToken(tokenRequest);
        ResponseCookie responseCookie = cookieService.createCookie(tokenResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(tokenResponse);
    }

    @Operation(
            summary = "Check login status",
            description = "Check whether the user is logged in",
            tags = {"Login API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> authorizeLogin(
            @Parameter(description = "Login member information", required = true) LoginMember loginMember) {
        final MemberResponse response = memberService.checkLogin(loginMember);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Logout",
            description = "Logout the currently logged-in user",
            tags = {"Login API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        final ResponseCookie responseCookie = cookieService.createEmptyCookie();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

    @Operation(
            summary = "Find all members",
            description = "Retrieve a list of all registered members",
            tags = {"Login API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findAllMembers() {
        List<MemberResponse> members = memberService.findAll();
        return ResponseEntity.ok(members);
    }
}
