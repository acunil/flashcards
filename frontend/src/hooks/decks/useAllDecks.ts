import { useState, useRef, useCallback, useEffect } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useAllDecks = (subjectId: number | null) => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();
  const lastFetchedId = useRef<number | null>(null);

  const fetchDecks = useCallback(
    async (force = false) => {
      if (subjectId === null) {
        setDecks([]);
        setLoading(false);
        lastFetchedId.current = null;
        return;
      }

      if (!force && lastFetchedId.current === subjectId) return; // skip if already fetched

      setLoading(true);
      setError(null);

      try {
        const data: Deck[] | undefined = await authFetch(
          `${API_URL}/decks?subjectId=${subjectId}`
        );

        setDecks(data ?? []);
        lastFetchedId.current = subjectId;
      } catch (err: unknown) {
        if (err instanceof Error) setError(err.message);
        else setError("Unknown error occurred");
      } finally {
        setLoading(false);
      }
    },
    [subjectId, authFetch]
  );

  // Automatically fetch on mount / subject change
  useEffect(() => {
    fetchDecks();
  }, [fetchDecks]);

  return { decks, loading, error, fetchDecks };
};

export default useAllDecks;
