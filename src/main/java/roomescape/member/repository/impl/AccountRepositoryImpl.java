package roomescape.member.repository.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.member.domain.Account;
import roomescape.member.domain.MemberEmail;
import roomescape.member.repository.AccountRepository;
import roomescape.member.repository.JpaAccountRepository;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    @Override
    public Account save(Account account) {
        return jpaAccountRepository.save(account);
    }

    @Override
    public Optional<Account> findAccountByMemberEmail(MemberEmail email) {
        return jpaAccountRepository.findAccountByMemberEmail(email);
    }
}
