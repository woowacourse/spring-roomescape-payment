package roomescape.waiting.domain;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "예약 대기 및 순위", description = "예약 대기 객체와 몇번째 대기인지를 전달한다.")
public record WaitingWithRank(Waiting waiting, Long rank) {

    public WaitingWithRank(Waiting waiting, int rank) {
        this(waiting, (long) rank);
    }
}
