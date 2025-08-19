import { useState } from "react";
import { API_URL } from "../urls";

interface UpdateCardPayload {
  id: number;
  front: string;
  back: string;
  deckNames: string[];
}

interface UpdateCardResult {
  isLoading: boolean;
  error: string | null;
  updateCard: (data: UpdateCardPayload) => Promise<void>;
}

const useUpdateCard = (): UpdateCardResult => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateCard = async (data: UpdateCardPayload) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await fetch(`${API_URL}/cards/${data.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        // You can customize error handling here
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to update card");
      }
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
        throw err; // rethrow so caller can handle if needed
      } else {
        setError("Unknown error");
        throw new Error("Unknown error");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return { isLoading, error, updateCard };
};

export default useUpdateCard;
