import { useState } from "react";
import { API_URL } from "../urls";
import type { SaveSubjectPayload } from "./useUpdateSubject";
import { useAuthFetch } from "../../utils/authFetch";

// Subject should include id
export interface Subject extends SaveSubjectPayload {
  id: number;
}

interface CreateSubjectResult {
  isLoading: boolean;
  error: string | null;
  createSubject: (
    data?: Partial<Omit<SaveSubjectPayload, "id">>
  ) => Promise<Subject>;
}

const useCreateSubject = (): CreateSubjectResult => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  const createSubject = async (
    data?: Partial<Omit<SaveSubjectPayload, "id">>
  ): Promise<Subject> => {
    setIsLoading(true);
    setError(null);

    const payload: Omit<SaveSubjectPayload, "id"> = {
      name: data?.name || "New Subject",
      frontLabel: data?.frontLabel || "Front",
      backLabel: data?.backLabel || "Back",
      defaultSide: "FRONT",
      displayDeckNames: false,
    };

    try {
      const newSubject: Subject = await authFetch(`${API_URL}/subjects`, {
        method: "POST",
        body: JSON.stringify(payload),
      });

      return newSubject; // guaranteed to have id from server
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
