import { useState } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useCreateDeck = (subjectId: number | null) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  /**
   * Create a new deck for the given subject
   * @param name Deck name
   */
  const createDeck = async (name: string) => {
    if (!subjectId) {
      setError("No subject selected");
      return null;
    }

    setLoading(true);
    setError(null);

    try {
      const data: Deck[] | undefined = await authFetch(
        `${API_URL}/decks?subjectId=${subjectId}`,
        {
          method: "POST",
          body: JSON.stringify([name]),
        }
      );

      if (!data) {
        // User was likely redirected to login
        return null;
      }

      // Return the first deck created
      return data[0] || null;
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Unknown error occurred");
      return null;
    } finally {
      setLoading(false);
    }
  };

  return { createDeck, loading, error };
};

export default useCreateDeck;
