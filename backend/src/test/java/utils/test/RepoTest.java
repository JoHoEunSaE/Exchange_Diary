package utils.test;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import utils.bean.JPAQueryFactoryConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DataJpaTest
@Import(JPAQueryFactoryConfig.class)
public class RepoTest {
	@PersistenceContext
	protected EntityManager em;
}
