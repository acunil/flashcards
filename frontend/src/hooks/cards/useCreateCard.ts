import { useState } from "react";
import { API_URL } from "../urls";

export type NewCard = {
  front: string;
  back: string;
  deckNames: string[];
};

const useCreateCard = () => {
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const createCard = async (card: NewCard) => {
    try {
      setCreating(true);
      setError(null);

      const response = await fetch(`${API_URL}/cards`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(card),
      });

      if (!response.ok) {
        throw new Error("Failed to create card");
      }

      const data = await response.json();
      return data;
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Unknown error occurred");
      }
      throw err; // propagate error if caller wants to handle it
    } finally {
      setCreating(false);
    }
  };

  return { createCard, creating, error };
};

export default useCreateCard;
