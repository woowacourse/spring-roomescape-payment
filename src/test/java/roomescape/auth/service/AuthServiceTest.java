package roomescape.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.createAdminMember;
import static roomescape.TestFixture.createDefaultMember_1;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTest;
import roomescape.auth.dto.LoginCheckResponse;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.dto.LoginRequest;
import roomescape.exception.ForbiddenException;
import roomescape.exception.UnauthorizedException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.domain.Password;

@Transactional
class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @DisplayName("이메일/비밀번호가 올바를 때 토큰 반환")
    @Test
    void createToken() {
        // given
        Member member = Member.builder()
                .name("member")
                .email("member@email.com")
                .password(Password.createForMember("password"))
                .role(MemberRole.MEMBER)
                .build();
        dbHelper.insertMember(member);

        // when
        LoginRequest loginRequest = new LoginRequest("member@email.com", "password");
        String token = authService.createToken(loginRequest);

        // then
        assertThat(token).isNotNull();
    }

    @DisplayName("이메일 or 비밀번호가 틀렸을 때 UnauthorizedException")
    @Test
    void createToken_error() {
        // given
        Member member = Member.builder()
                .name("member")
                .email("member@email.com")
                .password(Password.createForMember("password"))
                .role(MemberRole.MEMBER)
                .build();
        dbHelper.insertMember(member);

        // when & then
        LoginRequest loginRequest = new LoginRequest("member@email.com", "wrongPassword");

        assertThatThrownBy(() -> authService.createToken(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("이메일 또는 패스워드가 올바르지 않습니다.");
    }

    @DisplayName("유효한 토큰일 때 정상적으로 사용자 정보를 반환")
    @Test
    void checkLogin() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        LoginRequest loginRequest = new LoginRequest(member.getEmail(), member.getPassword());
        String token = authService.createToken(loginRequest);

        // when
        LoginCheckResponse response = authService.checkLogin(token);

        // then
        assertThat(response.name()).isEqualTo(member.getName());
    }

    @DisplayName("관리자 체크 시 관리자일 경우 아무 예외 없음")
    @Test
    void checkAdmin() {
        // given
        Member admin = dbHelper.insertMember(createAdminMember());

        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("memberId", admin.getId());

        // when & then
        authService.checkAdmin(request);
    }

    @DisplayName("관리자 체크 시 관리자 권한이 아니라면 권한없음 예외")
    @Test
    void checkAdmin_error_forbidden() {
        // given
        Member notAdminMember = dbHelper.insertMember(createDefaultMember_1());

        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("memberId", notAdminMember.getId());

        // when & then
        assertThatThrownBy(() -> authService.checkAdmin(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자 권한이 필요합니다.");
    }

    @DisplayName("memberId가 존재하고, 해당 회원이 존재할 경우 LoginMember 반환")
    @Test
    void extractMemberByRequest() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());

        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("memberId", member.getId());

        // when
        LoginMember loginMember = authService.extractMemberByRequest(request);

        // then
        assertThat(loginMember.id()).isEqualTo(member.getId());
        assertThat(loginMember.name()).isEqualTo(member.getName());
        assertThat(loginMember.email()).isEqualTo(member.getEmail());
        assertThat(loginMember.role()).isEqualTo(member.getRole());
    }

    @DisplayName("request에 memberId 없음: 인증 예외")
    @Test
    void extractMemberByRequest_error_notExistsMemberId() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        HttpServletRequest request = new MockHttpServletRequest();

        // when & then
        assertThatThrownBy(() -> authService.extractMemberByRequest(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증에 실패했습니다.");
    }

    @DisplayName("request의 memberId 타입이 잘못 됨: 인증 예외")
    @Test
    void extractMemberByRequest_error_memberIdParseFail() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("memberId", "invalid");

        // when & then
        assertThatThrownBy(() -> authService.extractMemberByRequest(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증에 실패했습니다.");
    }

    @DisplayName("회원 추출 시 회원이 존재하지 않음 → UnauthorizedException")
    @Test
    void extractMemberByRequest_error_notFoundMember() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("memberId", 999L);

        // when & then
        assertThatThrownBy(() -> authService.extractMemberByRequest(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증에 실패했습니다.");
    }
}
