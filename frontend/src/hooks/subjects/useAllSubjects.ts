import { useEffect, useRef, useState } from "react";
import { API_URL } from "../urls";
import type { Subject } from "../../types/subject";
import { useAuth0 } from "@auth0/auth0-react";

const useAllSubjects = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetched = useRef(false);
  const [selectedSubject, setSelectedSubject] = useState<Subject | undefined>(
    undefined
  );
  const { getAccessTokenSilently } = useAuth0();

  useEffect(() => {
    const fetchSubjects = async () => {
      if (hasFetched.current) return;
      hasFetched.current = true;

      try {
        setLoading(true);
        const token = await getAccessTokenSilently();
        const response = await fetch(`${API_URL}/subjects`, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        if (!response.ok) {
          throw new Error("Failed to fetch subjects");
        }
        const data: Subject[] = await response.json();
        setSubjects(data);
        setSelectedSubject(data[0]);
      } catch (err: unknown) {
        setError(err instanceof Error ? err.message : "Unknown error occurred");
      } finally {
        setLoading(false);
      }
    };

    fetchSubjects();
  }, [getAccessTokenSilently]); // 👈 only run once

  return { subjects, selectedSubject, loading, error };
};

export default useAllSubjects;
