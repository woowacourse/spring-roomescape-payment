package roomescape.integration.helper;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestAssuredRequestUtils {

    // === 기본 요청 ===
    public static Response sendGet(String uri, RequestSpecification spec) {
        return RestAssured.given(spec).log().all()
                .when().get(uri);
    }

    public static Response sendPost(String uri, Object body, RequestSpecification spec) {
        return RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(uri);
    }

    public static Response sendDelete(String uri, RequestSpecification spec, Object... pathParams) {
        return RestAssured.given(spec).log().all()
                .when().delete(uri, pathParams);
    }

    // === 인증 토큰 포함 요청 ===
    public static Response sendGetWithToken(String uri, RequestSpecification spec, String token) {
        return RestAssured.given(spec).log().all()
                .cookie("token", token)
                .when().get(uri);
    }

    public static Response sendPostWithToken(String uri, Object body, RequestSpecification spec, String token) {
        return RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(body)
                .when().post(uri);
    }

    public static Response sendDeleteWithToken(String uri, RequestSpecification spec, String token, Object... pathParams) {
        return RestAssured.given(spec).log().all()
                .cookie("token", token)
                .when().delete(uri, pathParams);
    }

    // === 필터 포함 요청 ===
    public static Response sendGetWithFilter(String uri, RequestSpecification spec, Filter filter) {
        return RestAssured.given(spec).log().all()
                .filters(filter)
                .when().get(uri);
    }

    public static Response sendPostWithFilter(String uri, Object body, RequestSpecification spec, Filter filter) {
        return RestAssured.given(spec).log().all()
                .filters(filter)
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(uri);
    }

    public static Response sendDeleteWithFilter(String uri, RequestSpecification spec, Filter filter, Object... pathParams) {
        return RestAssured.given(spec).log().all()
                .filters(filter)
                .contentType(ContentType.JSON)
                .when().delete(uri, pathParams);
    }

    // === 필터 + 토큰 포함 요청 ===
    public static Response sendGetWithTokenAndFilter(String uri, RequestSpecification spec, String token, Filter filter) {
        return RestAssured.given(spec).log().all()
                .filters(filter)
                .cookie("token", token)
                .when().get(uri);
    }

    public static Response sendPostWithTokenAndFilter(String uri, Object body, RequestSpecification spec, String token, Filter filter) {
        return RestAssured.given(spec).log().all()
                .filters(filter)
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(body)
                .when().post(uri);
    }

    public static Response sendDeleteWithTokenAndFilter(String uri, RequestSpecification spec, String token, Filter filter, Object... pathParams) {
        return RestAssured.given(spec).log().all()
                .cookie("token", token)
                .filters(filter)
                .when().delete(uri, pathParams);
    }
}
