package cc.vileda.rdrctr.redirecter.boundary;

import cc.vileda.rdrctr.NotFoundException;
import cc.vileda.rdrctr.redirecter.entity.Redirect;
import cc.vileda.rdrctr.redirecter.entity.RedirectLog;
import cc.vileda.rdrctr.redirecter.entity.Redirect_;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


@Stateless
public class Redirects {
    @PersistenceContext(unitName = "h2")
    private EntityManager em;

    public Redirect findByFromHost(String... fromHosts) throws NotFoundException {
        CriteriaQuery<Redirect> query = getRedirectCriteriaQuery(fromHosts);
        Redirect redirect;
        try {
            redirect = em.createQuery(query).getSingleResult();
        } catch (NoResultException nre) {
            throw new NotFoundException();
        }
        return redirect;
    }

    private CriteriaQuery<Redirect> getRedirectCriteriaQuery(String[] fromHosts) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Redirect> query = criteriaBuilder.createQuery(Redirect.class);
        Predicate predicate = criteriaBuilder.disjunction();
        Root<Redirect> from = query.from(Redirect.class);

        for (String fromHost : fromHosts) {
            predicate = criteriaBuilder.or(predicate,
                    criteriaBuilder.equal(from.get(Redirect_.fromHost), fromHost));
        }

        query.where(predicate);
        return query;
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

    public RedirectLog logRedirect(Redirect redirect, String referer, String fromHost, String toHost, String ip) {
        RedirectLog redirectLog = new RedirectLog(redirect, referer, fromHost, toHost, ip);
        return em.merge(redirectLog);
    }
}
