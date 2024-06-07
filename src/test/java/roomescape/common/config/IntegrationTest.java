package roomescape.common.config;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.domain.Role;
import roomescape.auth.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected RequestSpecification spec;

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder()
                .addFilter(RestAssuredRestDocumentation.documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withResponseDefaults(prettyPrint()))
                .build();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                        .uris()
                        .withPort(8080)
                        .and()
                        .operationPreprocessors()
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @AfterEach
    void databaseCleanUp() {
        databaseCleaner.cleanUp();
    }

    protected String getAdminToken() {
        Member member = new Member(1L, Role.ADMIN, new MemberName("어드민"), "admin@email.com", "1234");
        return jwtTokenProvider.generateToken(member);
    }

    protected String getMemberToken() {
        Member member = new Member(1L, Role.MEMBER, new MemberName("카키"), "kaki@email.com", "1234");
        return jwtTokenProvider.generateToken(member);
    }

    protected String getToken(Member member) {
        return jwtTokenProvider.generateToken(member);
    }

    protected void saveMemberAsKaki() {
        String sql = "insert into member (name, email, password, role) values ('카키', 'kaki@email.com', '1234', 'MEMBER')";

        jdbcTemplate.update(sql);
    }

    protected void saveAdminMember() {
        String sql = "insert into member (name, email, password, role) values ('어드민', 'admin@email.com', '1234', 'ADMIN')";

        jdbcTemplate.update(sql);
    }

    protected void saveMember(Member member) {
        String name = member.getName();
        String email = member.getEmail();
        String password = member.getPassword();
        String role = member.getRole().name();

        String sql = "insert into member (name, email, password, role) values (?, ?, ?, ?)";

        jdbcTemplate.update(sql, name, email, password, role);
    }

    protected void saveThemeAsHorror() {
        String sql = "insert into theme(name, description, thumbnail) values ('공포', '무서운 테마', 'https://a.com/a.jpg')";

        jdbcTemplate.update(sql);
    }

    protected void saveReservationTimeAsTen() {
        String sql = "insert into reservation_time (start_at) values ('10:00')";

        jdbcTemplate.update(sql);
    }

    protected void saveSuccessReservationAsDateNow() {
        String sql = "insert into reservation (member_id, date, theme_id, time_id, status) values (1, CURRENT_DATE, 1, 1, 'SUCCESS')";

        jdbcTemplate.update(sql);
    }

    protected void saveWaitReservationAsDateNow() {
        String sql = "insert into reservation (member_id, date, theme_id, time_id, status) values (1, CURRENT_DATE, 1, 1, 'WAIT')";

        jdbcTemplate.update(sql);
    }
}
