package com.images.images;

import com.images.images.models.MetaData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(MetaData metaData) {
        entityManager.persist(metaData);
    }
    public List<MetaData> getAllMetaData() {
        TypedQuery<MetaData> query = entityManager.createQuery("SELECT m FROM MetaData m ORDER BY m.createdDate DESC", MetaData.class);

        return query.getResultList();
    }
    @Transactional
    public void deleteMetaData(Long id) {
        MetaData metaData = findOne(id);
        if (metaData != null) {
            entityManager.remove(metaData);
        } else {
            throw new EntityNotFoundException("Metadata with ID " + id + " not found");
        }
    }
    public MetaData findOne(Long id){
        return entityManager.find(MetaData.class, id);
    }

}
