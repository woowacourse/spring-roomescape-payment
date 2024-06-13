package roomescape.common.config;

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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import roomescape.auth.domain.Role;
import roomescape.auth.jwt.JwtTokenProvider;
import roomescape.common.util.DatabaseCleaner;
import roomescape.common.util.MemberJdbcUtil;
import roomescape.common.util.PaymentJdbcUtil;
import roomescape.common.util.ReservationJdbcUtil;
import roomescape.common.util.ReservationTimeJdbcUtil;
import roomescape.common.util.ThemeJdbcUtil;
import roomescape.common.util.WaitingJdbcUtil;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
public class ControllerTest {

    protected RequestSpecification spec;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected MemberJdbcUtil memberJdbcUtil;

    @Autowired
    protected ReservationJdbcUtil reservationJdbcUtil;

    @Autowired
    protected WaitingJdbcUtil waitingJdbcUtil;

    @Autowired
    protected ReservationTimeJdbcUtil reservationTimeJdbcUtil;

    @Autowired
    protected ThemeJdbcUtil themeJdbcUtil;

    @Autowired
    protected PaymentJdbcUtil paymentJdbcUtil;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
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
}
