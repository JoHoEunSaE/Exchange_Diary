package org.johoeunsae.exchangediary.diary.domain.invitation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class InvitationFactory {

	private final ApplicationContext context;
	private final List<InvitationSupplier> suppliers = new ArrayList<>();

	public InvitationFactory(ApplicationContext context) {
		this.context = context;
	}

	@EventListener(ApplicationReadyEvent.class)
	private void initSuppliers() {
		addSuppliers(context.getBeansOfType(InvitationSupplier.class).values());
	}

	public void addSuppliers(Collection<InvitationSupplier> suppliers) {
		this.suppliers.addAll(suppliers);
	}

	public Optional<Invitation> createInvitation(Long diaryId, Long memberId,
			InvitationType invitationType) {
		log.debug("Called createInvitation diaryId: {}, memberId: {}, invitationType: {}", diaryId,
				memberId,
				invitationType);
		return suppliers.stream()
				.filter(e -> e.supports(invitationType))
				.findFirst().map(e -> e.supply(diaryId, memberId, invitationType));
	}
}
