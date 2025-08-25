import { useState, useEffect, useRef } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useAllDecks = (subjectId: number | null) => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();
  const lastFetchedId = useRef<number | null>(null);

  useEffect(() => {
    if (subjectId === null) {
      setDecks([]);
      setLoading(false);
      lastFetchedId.current = null;
      return;
    }

    // Only fetch if subjectId has changed
    if (lastFetchedId.current === subjectId) return;

    const fetchDecks = async () => {
      try {
        setLoading(true);
        setError(null);

        const data: Deck[] | undefined = await authFetch(
          `${API_URL}/decks?subjectId=${subjectId}`
        );

        if (!data) {
          setDecks([]);
          return;
        }

        setDecks(data);
        lastFetchedId.current = subjectId;
      } catch (err: unknown) {
        if (err instanceof Error) setError(err.message);
        else setError("Unknown error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchDecks();
  }, [subjectId, authFetch]);

  return { decks, loading, error };
};

export default useAllDecks;
