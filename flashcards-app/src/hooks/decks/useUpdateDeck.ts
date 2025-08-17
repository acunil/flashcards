import { useState } from "react";

const BASE_DECK_URL = "http://localhost:8080/decks";

const useUpdateDeck = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateDeckName = async (id: number, newName: string) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${BASE_DECK_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: newName }),
      });
      if (!response.ok) {
        throw new Error("Failed to update deck");
      }
      return await response.json(); // Return updated deck
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Unknown error occurred");
      return null;
    } finally {
      setLoading(false);
    }
  };

  return { updateDeckName, loading, error };
};

export default useUpdateDeck;
