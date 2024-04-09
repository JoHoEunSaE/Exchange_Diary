package org.johoeunsae.exchangediary.utils.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@MappedSuperclass
@ToString
@FieldNameConstants
public abstract class IdentityIdDomain extends IdDomain<Long> {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@Override
	public Long getId() {
		return id;
	}
}