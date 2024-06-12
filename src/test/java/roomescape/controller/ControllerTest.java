package roomescape.controller;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.Fixture.VALID_ADMIN_EMAIL;
import static roomescape.Fixture.VALID_USER_EMAIL;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import roomescape.infrastructure.auth.JwtProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
@SqlMergeMode(MergeMode.MERGE)
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
abstract class ControllerTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private JwtProvider jwtProvider;
    @LocalServerPort
    private int port;
    protected RequestSpecification spec;

    @BeforeEach
    protected void iniTest(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        Filter documentationConfiguration = documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration)
                .build();
    }

    protected String getUserToken() {
        return jwtProvider.createToken(VALID_USER_EMAIL.getEmail());
    }

    protected String getAdminToken() {
        return jwtProvider.createToken(VALID_ADMIN_EMAIL.getEmail());
    }
}
