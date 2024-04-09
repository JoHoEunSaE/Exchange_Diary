package utils.testdouble;

import org.johoeunsae.exchangediary.utils.domain.IdDomain;

public interface TestEntity<E extends IdDomain<?>, ID> {

	E asEntity();

	E asMockEntity(ID id);
}
