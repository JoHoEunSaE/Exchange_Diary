package org.johoeunsae.exchangediary.utils.update;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import org.johoeunsae.exchangediary.utils.domain.IdDomain;

/**
 * 업데이트 요청을 처리하는 클래스
 * 이 클래스는 유효성 검증 로직과 업데이트 로직을 큐로 저장한다.
 * 업데이트 요청을 받으면 유효성 검증과 업데이트 로직이 하나씩 추가된다.
 * register()를 통해 업데이트를 할 엔티티를 저장한다. (이 때 유효성 검증이 되고 업데이트 되지 않은 요청이 있다면 예외를 던진다.)
 * validate()를 통해 유효성 검증을 수행한다.
 * apply()를 통해 업데이트를 수행한다. (이 때 아직 유효성 검증이 되지 않은 요청이 있다면 예외를 던진다.)
 *
 * @param <T> 업데이트를 할 엔티티의 타입
 */
public class UpdateRequest<T extends IdDomain<?>> {
	private T entity = null;
	private final Queue<UpdateValidator<T>> validators = new LinkedList<>();
	private final Queue<UpdateApplier<T>> appliers = new LinkedList<>();

	/**
	 * 업데이트 요청을 처리한다.
	 * 업데이트 처리가 완료되면 업데이트된 엔티티를 반환하고 엔티티는 null로 초기화한다.
	 * 유효성 검증이 되지 않은 값이 있다면 실행할 수 없다.
	 * @return 업데이트된 엔티티
	 */
	public T apply() {
		if (!isValidated()) {
			throw new IllegalStateException("validate() must be called before apply()");
		}
		while (!appliers.isEmpty()) {
			appliers.poll().apply(getEntity());
		}
		T ret = entity;
		entity = null;
		return ret;
	}

	/**
	 * 업데이트를 할 엔티티를 등록한다.
	 * 유효성 검증이 된 값이 없다면 실행할 수 없다.
	 * @param entity 업데이트를 할 엔티티
	 */
	public void register(T entity) {
		if (!isSafe()) {
			throw new IllegalStateException("there is an unevaluated applier");
		}
		this.entity = entity;
	}

	/**
	 * 유효성 검증을 수행한다.
	 * 업데이트를 할 엔티티가 등록되지 않았다면 실행할 수 없다.
	 * @throws UpdateException
	 */
	public void validate() throws UpdateException {
		if (!isRegistered()) {
			throw new IllegalStateException("register must be called before validate()");
		}
		while (!validators.isEmpty()) {
			validators.poll().validate(entity);
		}
	}

	/**
	 * 유효성 검증이 완료되었는지 확인한다.
	 */
	public boolean isValidated() {
		return isRegistered() && validators.isEmpty();
	}

	/**
	 * 업데이트를 할 엔티티가 등록되었는지 확인한다.
	 */
	public boolean isRegistered() {
		return Objects.nonNull(entity);
	}

	/**
	 * 검증만되고 업데이트는 되지 않은 로직이 있는지 확인한다
	 * 다른 entity로 바꿀 수 있는 상태를 safe하다고 한다.
	 */
	public boolean isSafe() {
		return validators.size() == appliers.size();
	}

	/**
	 * 업데이트 요청을 처리하는 클래스의 내부 구현을 위한 메소드들
	 * validator가 추가되었다면 반드시 applier도 추가되어야한다.
	 * 외부에서는 사용하지 않는다.
	 *
	 * @param validator 업데이트 요청을 검증하는 클래스
	 */
	protected void addValidator(UpdateValidator<T> validator, UpdateApplier<T> applier) {
		addApplier(applier);
		validators.add(validator);
	}

	/**
	 * 업데이트 요청을 처리하는 클래스의 내부 구현을 위한 메소드들
	 * 외부에서는 사용하지 않는다.
	 */
	protected void addApplier(UpdateApplier<T> applier) {
		appliers.add(applier);
	}
	protected T getEntity() {
		return entity;
	}
}
