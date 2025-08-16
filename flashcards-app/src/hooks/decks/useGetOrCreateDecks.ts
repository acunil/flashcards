import { useCallback, useState } from "react";
import type { Deck } from "../../types/deck";

const BASE_DECK_URL = "http://localhost:8080/api/decks";

const useGetOrCreateDecks = () => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const getOrCreateDecks = useCallback(async (names: string[]) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(BASE_DECK_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ names }),
      });
      if (!response.ok) {
        throw new Error("Failed to get or create decks");
      }
      const data: Deck[] = await response.json();
      setDecks(data);
      return data;
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Unknown error occurred");
      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  return { decks, getOrCreateDecks, loading, error };
};

export default useGetOrCreateDecks;
