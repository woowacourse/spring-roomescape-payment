package roomescape.business.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.Id;
import roomescape.exception.member.DuplicatedEmailException;
import roomescape.exception.member.MemberNotFoundException;
import roomescape.infrastructure.MemberRepository;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.MemberResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse register(RegisterRequest request) {
        if (memberRepository.existsByEmail_Value(request.email())) {
            throw new DuplicatedEmailException();
        }
        Member member = Member.create(request.name(), request.email(), request.password());
        memberRepository.save(member);
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(final String userIdValue) {
        Member member = memberRepository.findById(Id.create(userIdValue))
                .orElseThrow(MemberNotFoundException::new);
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getByEmail(final String email) {
        Member member = memberRepository.findByEmail_Value(email)
                .orElseThrow(MemberNotFoundException::new);
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAll() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }
}
