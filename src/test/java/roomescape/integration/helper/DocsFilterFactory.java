package roomescape.integration.helper;

import io.restassured.filter.Filter;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.restdocs.snippet.Snippet;

public class DocsFilterFactory {

    public static Filter createDocumentFilter(String baseDir, String name, Snippet... snippets) {
        return RestAssuredRestDocumentation.document(String.join("/", baseDir, name), snippets);
    }
}
