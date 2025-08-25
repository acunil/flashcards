import { useEffect, useState } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useDeck = (id: number) => {
  const [deck, setDeck] = useState<Deck | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  useEffect(() => {
    if (!id) {
      setDeck(null);
      setLoading(false);
      return;
    }

    const fetchDeck = async () => {
      try {
        setLoading(true);
        setError(null);

        const data: Deck | undefined = await authFetch(
          `${API_URL}/decks/${id}`
        );

        if (!data) {
          // User was likely redirected to login
          setDeck(null);
          return;
        }

        setDeck(data);
      } catch (err: unknown) {
        if (err instanceof Error) setError(err.message);
        else setError("Unknown error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchDeck();
  }, [id, authFetch]);

  return { deck, loading, error };
};

export default useDeck;
