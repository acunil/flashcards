import { useState, useEffect, useCallback } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";

const useAllDecks = (subjectId: number | null) => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDecks = useCallback(
    async (id: number | null = subjectId) => {
      if (id === null) {
        setDecks([]);
        return;
      }

      try {
        setLoading(true);
        setError(null);
        const response = await fetch(`${API_URL}/decks?subjectId=${id}`);
        if (!response.ok) {
          throw new Error("Failed to fetch decks");
        }
        const data: Deck[] = await response.json();
        setDecks(data);
      } catch (err: unknown) {
        if (err instanceof Error) setError(err.message);
        else setError("Unknown error occurred");
      } finally {
        setLoading(false);
      }
    },
    [subjectId]
  );

  // Fetch decks whenever subjectId changes
  useEffect(() => {
    fetchDecks(subjectId);
  }, [subjectId, fetchDecks]);

  return { decks, loading, error, fetchDecks };
};

export default useAllDecks;
