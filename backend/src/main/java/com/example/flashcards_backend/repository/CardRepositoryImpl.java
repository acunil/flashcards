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
            WITH inserted AS (
                INSERT INTO card (front, back)
                VALUES (:front, :back)
                ON CONFLICT (front, back) DO NOTHING
                RETURNING id, front, back
            )
            SELECT id, front, back FROM inserted
            UNION ALL
            SELECT id, front, back FROM card
            WHERE front = :front AND back = :back
            AND NOT EXISTS (SELECT 1 FROM inserted)
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("front", front);
        query.setParameter("back", back);

        Object[] result = (Object[]) query.getSingleResult();

        Map<String, Object> map = new HashMap<>();
        map.put("id", result[0]);
        map.put("front", result[1]);
        map.put("back", result[2]);
        return map;
    }
}