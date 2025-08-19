import { useEffect, useState } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";

const useDecksByCardId = (cardId: number) => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!cardId) return;

    const fetchDecks = async () => {
      try {
        setLoading(true);
        const response = await fetch(`${API_URL}/decks/card/${cardId}`);
        if (!response.ok) {
          throw new Error("Failed to fetch decks by card ID");
        }
        const data: Deck[] = await response.json();
        setDecks(data);
      } catch (err: unknown) {
        if (err instanceof Error) setError(err.message);
        else setError("Unknown error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchDecks();
  }, [cardId]);

  return { decks, loading, error };
};

export default useDecksByCardId;
