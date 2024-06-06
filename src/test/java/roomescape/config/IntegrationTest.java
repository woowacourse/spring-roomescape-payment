package roomescape.config;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

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
import roomescape.auth.domain.Role;
import roomescape.auth.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
public abstract class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @AfterEach
    void databaseCleanUp() {
        databaseCleaner.cleanUp();
    }

    protected String getAdminToken() {
        Member member = new Member(1L, Role.ADMIN, new MemberName("러너덕"), "duck@email.com", "1234");
        return jwtTokenProvider.generateToken(member);
    }

    protected String getMemberToken() {
        Member member = new Member(1L, Role.USER, new MemberName("카키"), "kaki@email.com", "1234");
        return jwtTokenProvider.generateToken(member);
    }

    protected void saveMemberAsKaki() {
        String sql = "insert into member (name, email, password, role) values ('카키', 'kaki@email.com', '1234', 'USER')";

        jdbcTemplate.update(sql);
    }

    protected void saveAdminMemberAsDuck() {
        String sql = "insert into member (name, email, password, role) values ('러너덕', 'duck@email.com', '1234', 'ADMIN')";

        jdbcTemplate.update(sql);
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
        String sql = "insert into reservation (member_id, date, theme_id, time_id, status, created_at) values (1, CURRENT_DATE, 1, 1, 'SUCCESS', CURRENT_TIMESTAMP())";

        jdbcTemplate.update(sql);
    }

    protected void saveSuccessReservationAsDateTomorrow() {
        String sql = "insert into reservation (member_id, date, theme_id, time_id, status, created_at) values (1, DATEADD(DAY, 1, CURRENT_DATE), 1, 1, 'SUCCESS', CURRENT_TIMESTAMP())";

        jdbcTemplate.update(sql);
    }

    protected void saveWaitingAsDateTomorrow(){
        String sql = "insert into reservation (member_id, date, theme_id, time_id, status, created_at) values (2, DATEADD(DAY, 1, CURRENT_DATE), 1, 1, 'WAIT', CURRENT_TIMESTAMP())";

        jdbcTemplate.update(sql);
    }
}
