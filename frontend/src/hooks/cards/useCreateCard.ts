import { useState } from "react";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

export type NewCard = {
  front: string;
  back: string;
  hintFront?: string;
  hintBack?: string;
  deckNames: string[];
  subjectId: number;
};

const useCreateCard = () => {
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  const createCard = async (card: NewCard) => {
    if (!card.subjectId || card.subjectId === 0) {
      setError("No subject selected");
      return null;
    }

    setCreating(true);
    setError(null);

    try {
      const result = await authFetch(`${API_URL}/cards`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(card),
      });

      if (result === undefined) {
        // User was likely redirected to login
        return null;
      }

      return result;
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Unknown error occurred");
      }
      throw err;
    } finally {
      setCreating(false);
    }
  };

  return { createCard, creating, error };
};

export default useCreateCard;
