import { useState } from "react";
import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useCsvUpload = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  /**
   * Upload CSV for a given subject.
   * @param subjectId number
   * @param file CSV file
   */
  const uploadCsv = async (subjectId: number, file: File) => {
    setLoading(true);
    setError(null);

    try {
      const formData = new FormData();
      formData.append("file", file);

      // authFetch supports passing FormData; it will skip Content-Type override
      const data = await authFetch(`${API_URL}/csv/${subjectId}`, {
        method: "POST",
        body: formData,
      });

      if (!data) {
        // User was likely redirected to login
        return null;
      }

      return data;
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Unknown error occurred");
      return null;
    } finally {
      setLoading(false);
    }
  };

  return { uploadCsv, loading, error };
};

export default useCsvUpload;
