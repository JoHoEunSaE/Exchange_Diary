package org.johoeunsae.exchangediary.utils.domain;

import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Persistable;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Transient;
import java.io.Serializable;

@MappedSuperclass
public abstract class IdDomain<ID extends Serializable> implements Persistable<ID>{
    @Transient
    private Boolean isNew = true;

    @Override
    public boolean isNew() { return isNew; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getId() == null) {
            return false;
        }
        if (!(o instanceof HibernateProxy)
            && this.getClass() != o.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Serializable oid = o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getIdentifier() :
            ((IdDomain<ID>) o).getId();
        return getId().equals(oid);
    }

    @Override
    public int hashCode() {
        if (getId() == null)
            throw new IllegalStateException("id가 아직 null입니다");
        return getId().hashCode();
    }

    @PostLoad @PostPersist
    protected void markNotNew() {
        this.isNew = false;
    }
}

