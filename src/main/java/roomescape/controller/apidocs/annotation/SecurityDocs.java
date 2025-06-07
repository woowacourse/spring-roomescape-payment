package roomescape.controller.apidocs.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface SecurityDocs {

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "401", description = "유저 인증 처리 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "미로그인", value = """
                            {
                                "message": "로그인이 필요한 서비스입니다."
                            }
                            """)))
    @interface Unauthorized {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "403", description = "사용자 권한 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "권한 부족", value = """
                            {
                                "message": "접근 불가한 페이지입니다."
                            }
                            """)))
    @interface Forbidden {
    }

}
