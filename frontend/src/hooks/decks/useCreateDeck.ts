import { useState } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";

const useCreateDeck = (subjectId: number | null) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

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
      const response = await fetch(`${API_URL}/decks?subjectId=${subjectId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name }),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Failed to create deck");
      }

      const data: Deck = await response.json();
      return data;
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
