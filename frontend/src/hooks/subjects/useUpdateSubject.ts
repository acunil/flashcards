import { useState } from "react";
import { API_URL } from "../urls";

interface UpdateSubjectPayload {
  id: number;
  frontLabel: string;
  backLabel: string;
  defaultSide?: string;
}

interface UpdateSubjectResult {
  isLoading: boolean;
  error: string | null;
  updateSubject: (data: UpdateSubjectPayload) => Promise<void>;
}

const useUpdateSubject = (): UpdateSubjectResult => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateSubject = async (data: UpdateSubjectPayload) => {
    setIsLoading(true);
    setError(null);
    data.defaultSide = "FRONT";

    try {
      const response = await fetch(`${API_URL}/subjects/${data.id}`, {
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
