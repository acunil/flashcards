import { useState } from "react";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useDeleteDeck = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  const deleteDeck = async (id: number) => {
    setLoading(true);
    setError(null);

    try {
      const result = await authFetch(`${API_URL}/decks/${id}`, {
        method: "DELETE",
      });

      if (result === undefined) {
        // User was likely redirected to login
        return false;
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
