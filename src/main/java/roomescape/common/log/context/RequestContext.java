package roomescape.common.log.context;

public record RequestContext(
        String id,
        String ip,
        String method,
        String url
) {
}
