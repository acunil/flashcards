import { useCallback, useState } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useGetOrCreateDecks = () => {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  const getOrCreateDecks = useCallback(
    async (names: string[]) => {
      setLoading(true);
      setError(null);

      try {
        const data: Deck[] | undefined = await authFetch(`${API_URL}/decks`, {
          method: "POST",
          body: JSON.stringify({ names }),
        });

        if (!data) {
          // User was likely redirected to login
          return [];
        }

        setDecks(data);
        return data;
      } catch (err: unknown) {
        if (err instanceof Error) setError(err.message);
        else setError("Unknown error occurred");
        return [];
      } finally {
        setLoading(false);
      }
    },
    [authFetch]
  );

  return { decks, getOrCreateDecks, loading, error };
};

export default useGetOrCreateDecks;
