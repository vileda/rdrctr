package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.redirecter.entity.Redirect;
import cc.vileda.rdrctr.redirecter.entity.Redirect_;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Optional;


@Stateless
public class Redirects {
    @PersistenceContext(unitName = "h2")
    private EntityManager em;

    public Optional<Redirect> findByFromHost(String ... fromHosts) {
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Redirect> query = criteriaBuilder.createQuery(Redirect.class);
            Predicate predicate = criteriaBuilder.disjunction();
            Root<Redirect> from = query.from(Redirect.class);

            for (String fromHost : fromHosts) {
                predicate = criteriaBuilder.or(predicate,
                        criteriaBuilder.equal(from.get(Redirect_.fromHost), fromHost));
            }

            query.where(predicate);

            Redirect redirect = em.createQuery(query).getSingleResult();

            return Optional.of(redirect);
        } catch (NoResultException ignored) { }

        return Optional.empty();
    }

    public Redirect saveOrUpdate(Redirect redirect) {
        return em.merge(redirect);
    }

    public void incrementViewCount(Redirect redirect) {
        redirect.setViewCount(redirect.getViewCount()+1);
        saveOrUpdate(redirect);
    }

    public Redirect find(long id) {
        return em.find(Redirect.class, id);
    }
}
