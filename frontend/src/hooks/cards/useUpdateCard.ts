import { useState } from "react";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

interface UpdateCardPayload {
  id: number;
  front: string;
  back: string;
  hintFront?: string;
  hintBack?: string;
  subjectId: number;
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
  const { authFetch } = useAuthFetch();

  const updateCard = async (data: UpdateCardPayload) => {
    setIsLoading(true);
    setError(null);

    if (!data.subjectId || data.subjectId === 0) {
      setError("No subject selected");
      setIsLoading(false);
      return;
    }

    try {
      const result = await authFetch(`${API_URL}/cards/${data.id}`, {
        method: "PUT",
        body: JSON.stringify(data),
      });

      if (result === undefined) {
        // User was likely redirected to login
        return;
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
