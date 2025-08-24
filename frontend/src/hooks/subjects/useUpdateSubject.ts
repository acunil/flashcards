import { useState } from "react";
import { API_URL } from "../urls";

export interface SaveSubjectPayload {
  id?: number;
  name: string;
  frontLabel: string;
  backLabel: string;
  defaultSide?: string;
  displayDeckNames?: boolean;
}

interface UpdateSubjectResult {
  isLoading: boolean;
  error: string | null;
  updateSubject: (data: SaveSubjectPayload) => Promise<void>;
}

const useUpdateSubject = (): UpdateSubjectResult => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateSubject = async (data: SaveSubjectPayload) => {
    setIsLoading(true);
    setError(null);

    data.defaultSide = "FRONT";
    data.displayDeckNames = false;

    try {
      const response = await fetch(`${API_URL}/subjects`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        // You can customize error handling here
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to update subject");
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

  return { isLoading, error, updateSubject };
};

export default useUpdateSubject;
