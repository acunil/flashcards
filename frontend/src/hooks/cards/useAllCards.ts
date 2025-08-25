import { useEffect, useState, useCallback } from "react";
import type { Card } from "../../types/card";
import { API_URL } from "../urls";

const useAllCards = (subjectId: number | null) => {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Memoized fetch function
  const fetchCards = useCallback(async (subjectIdParam: number) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `${API_URL}/cards?subjectId=${subjectIdParam}`
      );
      if (!response.ok) throw new Error("Failed to fetch cards");
      const data: Card[] = await response.json();
      setCards(data);
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Unknown error occurred");
    } finally {
      setLoading(false);
    }
  }, []);

  // Re-fetch cards whenever the subjectId changes
  useEffect(() => {
    if (subjectId !== null) {
      fetchCards(subjectId);
    } else {
      setCards([]); // no subject selected, empty array
    }
  }, [subjectId, fetchCards]); // fetchCards is stable now

  return { cards, setCards, loading, error, refetch: fetchCards };
};

export default useAllCards;
