import { useState } from "react";
import { API_URL } from "../urls";

const useCsvUpload = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Upload CSV for a given subject.
   * @param subjectId number
   * @param file CSV file
   */
  const uploadCsv = async (subjectId: number, file: File) => {
    setLoading(true);
    setError(null);

    console.log(subjectId);

    try {
      const formData = new FormData();
      formData.append("file", file);

      const response = await fetch(`${API_URL}/csv/${subjectId}`, {
        method: "POST",
        body: formData,
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "CSV upload failed");
      }

      const data = await response.json();
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
