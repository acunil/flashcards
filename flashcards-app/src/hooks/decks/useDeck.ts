import { useEffect, useState } from "react";
import type { Deck } from "../../types/deck";

const BASE_DECK_URL = "http://localhost:8080/api/decks";

const useDeck = (id: string) => {
  const [deck, setDeck] = useState<Deck | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;

    const fetchDeck = async () => {
      try {
        setLoading(true);
        const response = await fetch(`${BASE_DECK_URL}/${id}`);
        console.log(response);
        if (!response.ok) {
          throw new Error("Failed to fetch deck");
        }
        const data: Deck = await response.json();
        setDeck(data);
      } catch (err: unknown) {
        if (err instanceof Error) setError(err.message);
        else setError("Unknown error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchDeck();
  }, [id]);

  return { deck, loading, error };
};

export default useDeck;
