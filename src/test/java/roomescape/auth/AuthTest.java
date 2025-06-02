package roomescape.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.controller.dto.SignupRequest;
import roomescape.member.service.AuthService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String memberToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        final String memberEmail = "hotteok@naver.com";
        final String memberPassword = "1234";
        final String adminEmail = "solar@naver.com";
        final String adminPassword = "1234";

        authService.signup(new SignupRequest(memberEmail, memberPassword, "호떡"));
        jdbcTemplate.update("""
                INSERT INTO member (name, email, role) VALUES (?, ?, ?)
                """, "솔라", adminEmail, "ADMIN");
        jdbcTemplate.update("""
                INSERT INTO account (password, member_id) VALUES (?, ?)
                """, passwordEncoder.encode(memberPassword), 2);

        memberToken = authService.login(new LoginRequest(memberEmail, memberPassword));
        adminToken = authService.login(new LoginRequest(adminEmail, adminPassword));
    }

    @Nested
    class PermitAllTest {

        @DisplayName("@PermitAll이 붙은 엔드포인트는 로그인하지 않은 사용자도 접근할 수 있다.")
        @Test
        void permitAll() throws Exception {
            mockMvc.perform(get("/test/permit-all"))
                    .andExpect(status().isOk());
        }

        @DisplayName("@PermitAll이 붙은 엔드포인트는 Member가 접근할 수 있다.")
        @Test
        void permitAllForMember() throws Exception {
            mockMvc.perform(get("/test/permit-all")
                            .cookie(new Cookie("token", memberToken)))
                    .andExpect(status().isOk());
        }

        @DisplayName("@PermitAll이 붙은 엔드포인트는 Admin이 접근할 수 있다.")
        @Test
        void permitAllForAdmin() throws Exception {
            mockMvc.perform(get("/test/permit-all")
                            .cookie(new Cookie("token", adminToken)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class RoleRequiredTest {

        @DisplayName("@RoleRequired(Role.ADMIN)가 붙은 엔드포인트에 Admin은 접근할 수 있다.")
        @Test
        void roleRequired() throws Exception {
            mockMvc.perform(get("/test/role-required-admin")
                            .cookie(new Cookie("token", adminToken)))
                    .andExpect(status().isOk());
        }

        @DisplayName("@RoleRequired(Role.ADMIN)가 붙은 엔드포인트에 로그인하지 않은 사용자는 접근할 수 없다.")
        @Test
        void roleRequiredForUser() throws Exception {
            mockMvc.perform(get("/test/role-required-admin"))
                    .andExpect(status().isUnauthorized());
        }

        @DisplayName("@RoleRequired(Role.ADMIN)가 붙은 엔드포인트에 Member는 접근할 수 없다.")
        @Test
        void roleRequiredForMember() throws Exception {
            mockMvc.perform(get("/test/role-required-admin")
                            .cookie(new Cookie("token", memberToken)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class LoginMemberTest {

        @DisplayName("@LoginMember를 붙이면 Member의 MemberInfo를 받아온다.")
        @Test
        void loginMemberForMember() throws Exception {
            mockMvc.perform(get("/test/login-member")
                            .cookie(new Cookie("token", memberToken)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                                {
                                    "id": 1,
                                    "name": "호떡",
                                    "email": "hotteok@naver.com",
                                    "role": "MEMBER"
                                }
                            """));
        }

        @DisplayName("@LoginMember를 붙이면 Admin의 MemberInfo를 받아온다.")
        @Test
        void loginMemberForAdmin() throws Exception {
            mockMvc.perform(get("/test/login-member")
                            .cookie(new Cookie("token", adminToken)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "id": 2,
                                "name": "솔라",
                                "email": "solar@naver.com",
                                "role": "ADMIN"
                            }
                            """));
        }

        @DisplayName("로그인하지 않은 유저가 @LoginMember(required = true)에 접근할 수 없다.")
        @Test
        void loginMemberForUserOrThrowIfRequiredTrue() throws Exception {
            mockMvc.perform(get("/test/login-member"))
                    .andExpect(status().isForbidden());
        }

        @DisplayName("로그인하지 않은 유저가 @LoginMember(required = false)에 접근할 수 있다.")
        @Test
        void loginMemberForUserOrThrowIfRequiredFalse() throws Exception {
            mockMvc.perform(get("/test/login-member-required-false"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class AdminPathTest {

        @DisplayName("/admin/** 경로에 Admin은 접근할 수 있다.")
        @Test
        void adminPath() throws Exception {
            mockMvc.perform(get("/admin/test")
                            .cookie(new Cookie("token", adminToken)))
                    .andExpect(status().isOk());
        }

        @DisplayName("/admin/** 경로에 Member는 접근할 수 없다.")
        @Test
        void adminPathForMember() throws Exception {
            mockMvc.perform(get("/admin/test")
                            .cookie(new Cookie("token", memberToken)))
                    .andExpect(status().isForbidden());
        }

        @DisplayName("/admin/** 경로에 로그인하지 않은 유저는 접근할 수 없다.")
        @Test
        void adminPathForUser() throws Exception {
            mockMvc.perform(get("/admin/test"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
