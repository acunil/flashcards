package com.example.flashcards_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

public class CardRepositoryImpl implements CardRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Map<String, Object> createIfUnique(String front, String back) {
        // Use a CTE to attempt the insert and fall back to selecting the existing row
        String sql = """
        INSERT INTO card (front, back)
        VALUES (:front, :back)
        ON CONFLICT (front, back) DO UPDATE SET front = EXCLUDED.front
        RETURNING id, front, back, (xmax != 0) AS already_existed
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("front", front);
        query.setParameter("back", back);

        Object[] result = (Object[]) query.getSingleResult();

        Map<String, Object> map = new HashMap<>();
        map.put("id", result[0]);
        map.put("front", result[1]);
        map.put("back", result[2]);
        map.put("alreadyExisted", result[3]);
        return map;
    }
}