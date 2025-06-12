package roomescape.integration.helper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

public class AuthTokenExtractor {

    public static RequestSpecification specWithLoginAdmin(RequestSpecification spec) {
        String token = extractAdminToken();
        return specWithLogin(token, spec);
    }

    public static RequestSpecification specWithLoginMember1(RequestSpecification spec) {
        String token = extractMember1Token();
        return specWithLogin(token, spec);
    }

    public static RequestSpecification specWithLoginMember2(RequestSpecification spec) {
        String token = extractMember2Token();
        return specWithLogin(token, spec);
    }

    public static String extractAdminToken() {
        return extractToken(Map.of("email", "admin@email.com", "password", "password"), "/admin/login");
    }

    public static String extractMember1Token() {
        return extractToken(Map.of("email", "member1@email.com", "password", "password"), "/login");
    }

    public static String extractMember2Token() {
        return extractToken(Map.of("email", "member2@email.com", "password", "password"), "/login");
    }

    private static String extractToken(Map<String, String> credentials, String loginUrl) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when().post(loginUrl)
                .then().statusCode(200)
                .extract();

        String token = response.cookie("token");
        if (token == null) {
            throw new IllegalStateException("토큰이 응답에 없습니다.");
        }
        return token;
    }

    private static RequestSpecification specWithLogin(String token, RequestSpecification spec) {
        return RestAssured.given(spec)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
}
