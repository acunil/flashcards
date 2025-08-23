import { useEffect, useRef, useState } from "react";
import { API_URL } from "../urls";
import type { Subject } from "../../types/subject";

const useSubjects = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetched = useRef(false);
  const [selectedSubject, setSelectedSubject] = useState<Subject | undefined>(
    undefined
  );

  useEffect(() => {
    const fetchSubjects = async () => {
      if (hasFetched.current) return;
      hasFetched.current = true;

      try {
        setLoading(true);
        const response = await fetch(
          `${API_URL}/subjects?userId=11111111-1111-1111-1111-111111111111`
        );
        if (!response.ok) {
          throw new Error("Failed to fetch cards");
        }
        const data: Subject[] = await response.json();
        setSubjects(data);
        setSelectedSubject(data[0]);
      } catch (err: unknown) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError("Unknown error occurred");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchSubjects();
  });

  return { subjects, selectedSubject, loading, error };
};

export default useSubjects;
