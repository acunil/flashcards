import { useState } from "react";
import { API_URL } from "../urls";

const useDeleteDeck = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const deleteDeck = async (id: number) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${API_URL}/decks/${id}`, {
        method: "DELETE",
      });
      if (!response.ok) {
        throw new Error("Failed to delete deck");
      }
      return true;
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Unknown error occurred");
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { deleteDeck, loading, error };
};

export default useDeleteDeck;
