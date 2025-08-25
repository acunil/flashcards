import { useEffect, useState } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useDecksByCardId = (cardId: number) => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  useEffect(() => {
    if (!cardId) {
      setDecks([]);
      setLoading(false);
      return;
    }

    const fetchDecks = async () => {
      try {
        setLoading(true);
        setError(null);

        const data: Deck[] | undefined = await authFetch(
          `${API_URL}/decks/card/${cardId}`
        );

        if (!data) {
          // User was likely redirected to login
          setDecks([]);
          return;
        }

        setDecks(data);
      } catch (err: unknown) {
        if (err instanceof Error) setError(err.message);
        else setError("Unknown error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchDecks();
  }, [cardId, authFetch]);

  return { decks, loading, error };
};

export default useDecksByCardId;
