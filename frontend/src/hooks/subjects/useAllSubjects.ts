import { useEffect, useRef, useState } from "react";
import { API_URL } from "../urls";
import type { Subject } from "../../types/subject";
import { useAuthFetch } from "../../utils/authFetch";

const useAllSubjects = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetched = useRef(false);
  const { authFetch } = useAuthFetch();

  useEffect(() => {
    const fetchSubjects = async () => {
      if (hasFetched.current) return;
      hasFetched.current = true;

      setLoading(true);
      try {
        const data: Subject[] = await authFetch(`${API_URL}/subjects`);
        setSubjects(data);
      } catch (err: unknown) {
        setError(err instanceof Error ? err.message : "Unknown error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchSubjects();
  }, [authFetch]);

  return { subjects, loading, error };
};
export default useAllSubjects;
