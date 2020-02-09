package pl.akademiaspring.week3.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.akademiaspring.week3.entity.Authority;
import pl.akademiaspring.week3.entity.types.AuthorityType;

@Repository
public interface AuthorityRepo extends JpaRepository<Authority, Long> {

    Authority findAuthorityByName(AuthorityType name);
}
