package com.tertiary.verify.data_model.model.entities.common;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.swing.*;
import org.springframework.stereotype.Service;

@Service
public class EntityDAOFacade {
  @PersistenceContext private EntityManager em;

  private Predicate getFilterCondition(
      CriteriaBuilder cb, Root<?> entityObject, Map<String, Object> filters) {
    Predicate filterCondition = cb.conjunction();
    String wildCard = "%";
    if (filters != null) {
      for (Map.Entry<String, Object> filter : filters.entrySet()) {
        String value = wildCard + filter.getValue() + wildCard;
        if (!filter.getValue().equals("")) {
          Path<String> path = entityObject.get(filter.getKey());

          if (path.getJavaType() != null) {
            if (path.getJavaType().getName().contains("com.sibiya")) // Searching by joined Entity
            {
              filterCondition = cb.and(filterCondition, cb.equal(path, filter.getValue()));
            } else {
              filterCondition = cb.and(filterCondition, cb.like(path, value));
            }
          } else {
            filterCondition = cb.and(filterCondition, cb.like(path, value));
          }
        }
      }
    }
    return filterCondition;
  }

  @SuppressWarnings("unchecked")
  public int count(Map<String, Object> filters, Class entityClass) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<?> entityObject = cq.from(entityClass);
    cq.where(getFilterCondition(cb, entityObject, filters));
    cq.select(cb.count(entityObject));
    return em.createQuery(cq).getSingleResult().intValue();
  }

  @SuppressWarnings("unchecked")
  public List<?> getResultList(
      Class entityClass,
      int first,
      int pageSize,
      String sortField,
      SortOrder sortOrder,
      Map<String, Object> filters) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(entityClass);
    Root<?> entityObject = cq.from(entityClass);
    cq.where(getFilterCondition(cb, entityObject, filters));
    if (sortField != null) {
      if (sortOrder == SortOrder.ASCENDING) {
        cq.orderBy(cb.asc(entityObject.get(sortField)));
      } else if (sortOrder == SortOrder.DESCENDING) {
        cq.orderBy(cb.desc(entityObject.get(sortField)));
      }
    }
    return em.createQuery(cq).setFirstResult(first).setMaxResults(pageSize).getResultList();
  }
}
