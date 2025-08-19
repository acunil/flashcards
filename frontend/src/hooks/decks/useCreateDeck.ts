import { useState } from "react";
import type { Deck } from "../../types/deck";
import { API_URL } from "../urls";

const useCreateDeck = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const createDeck = async (name: string) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${API_URL}/decks/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name }),
      });
      if (!response.ok) {
        throw new Error("Failed to create deck");
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
