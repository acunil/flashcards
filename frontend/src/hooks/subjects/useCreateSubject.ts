import { useState } from "react";
import { API_URL } from "../urls";
import type { SaveSubjectPayload } from "./useUpdateSubject";
import { useAuthFetch } from "../../utils/authFetch";

interface CreateSubjectResult {
  isLoading: boolean;
  error: string | null;
  createSubject: (
    data?: Partial<Omit<SaveSubjectPayload, "id">>
  ) => Promise<SaveSubjectPayload>;
}

const useCreateSubject = (): CreateSubjectResult => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  const createSubject = async (
    data?: Partial<Omit<SaveSubjectPayload, "id">>
  ): Promise<SaveSubjectPayload> => {
    setIsLoading(true);
    setError(null);

    // Default payload
    const payload: SaveSubjectPayload = {
      name: data?.name || "New Subject",
      frontLabel: data?.frontLabel || "Front",
      backLabel: data?.backLabel || "Back",
      defaultSide: "FRONT",
      displayDeckNames: false,
    };

    try {
      const response = await authFetch(`${API_URL}/subjects`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || "Failed to create subject");
      }

      const newSubject: SaveSubjectPayload = await response.json();
      return newSubject;
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
        throw err;
      } else {
        setError("Unknown error");
        throw new Error("Unknown error");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return { isLoading, error, createSubject };
};

export default useCreateSubject;
