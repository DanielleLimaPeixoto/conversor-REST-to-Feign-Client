package org.ufla.dcc.jmsnewsl.repository;

import org.springframework.data.repository.CrudRepository;
import org.ufla.dcc.jmsnewsl.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
