import { useState, useEffect, useCallback } from "react";
import type { Deck } from "../../types/deck";

const BASE_DECK_URL = "http://localhost:8080/api/decks";

const useAllDecks = () => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDecks = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch(BASE_DECK_URL);
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
  }, []);

  // Fetch decks on mount
  useEffect(() => {
    fetchDecks();
  }, [fetchDecks]);

  return { decks, loading, error, fetchDecks };
};

export default useAllDecks;
