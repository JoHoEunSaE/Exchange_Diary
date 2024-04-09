package org.johoeunsae.exchangediary.auth.oauth2.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component @Slf4j
public class Oauth2LoginFactory {
	private final ApplicationContext context;
	private final List<Oauth2LoginSupplier> suppliers = new ArrayList<>();

	public Oauth2LoginFactory(ApplicationContext context) {
		this.context = context;
	}
	@EventListener(ApplicationReadyEvent.class)
	public void initSuppliers() {
		log.debug("Called setSuppliers {}", suppliers);
		addSuppliers(context.getBeansOfType(Oauth2LoginSupplier.class).values());
	}

	public void addSuppliers(Collection<Oauth2LoginSupplier> suppliers) {
		this.suppliers.addAll(suppliers);
	}

	public Optional<Oauth2Login> create(Oauth2LoginRequestVO dto) {
		log.debug("Called getStrategy {}", dto);
		return suppliers.stream()
				.filter(e -> e.supports(dto))
				.findFirst().map(e -> e.supply(dto));
	}
}
