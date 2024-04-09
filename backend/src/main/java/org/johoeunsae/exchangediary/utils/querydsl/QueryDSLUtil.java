package org.johoeunsae.exchangediary.utils.querydsl;


import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface QueryDSLUtil {

	static List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable,
			Class<?> type) {

		Sort sort = pageable.getSort();

		// OrderSpecifier 생성
		String entityQName = type.getSimpleName().toLowerCase();
		PathBuilder<?> entityPath = new PathBuilder<>(type,
				entityQName);

		return sort.stream()
				.map(order -> {
					if (order.isAscending()) {
						return entityPath.getString(order.getProperty()).asc();
					} else {
						return entityPath.getString(order.getProperty()).desc();
					}
				})
				.collect(Collectors.toList());
	}

}
