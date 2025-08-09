import { useEffect, useState } from "react";

export type Deck = {
  id: string;
  name: string;
};

const BASE_DECK_URL = "http://localhost:8080/api/decks";

const useDecksByCardId = (cardId: string) => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!cardId) return;

    const fetchDecks = async () => {
      try {
        setLoading(true);
        const response = await fetch(`${BASE_DECK_URL}/card/${cardId}`);
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
