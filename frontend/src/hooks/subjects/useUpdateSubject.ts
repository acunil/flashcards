import { useState } from "react";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

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
  const { authFetch } = useAuthFetch();

  const updateSubject = async (data: SaveSubjectPayload) => {
    if (!data.id) {
      setError("Subject ID is required");
      return;
    }

    setIsLoading(true);
    setError(null);

    // Ensure default values
    data.defaultSide = "FRONT";
    data.displayDeckNames = false;

    try {
      const result = await authFetch(`${API_URL}/subjects/${data.id}`, {
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

  return { isLoading, error, updateSubject };
};

export default useUpdateSubject;
