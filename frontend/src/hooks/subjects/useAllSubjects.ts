import { useEffect, useRef, useState } from "react";
import { API_URL } from "../urls";
import type { Subject } from "../../types/subject";
import { useAuth0 } from "@auth0/auth0-react";
import { useAuthFetch } from "../../utils/authFetch";

const useAllSubjects = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetched = useRef(false);
  const [selectedSubject, setSelectedSubject] = useState<Subject | undefined>(
    undefined
  );
  const { getAccessTokenSilently, loginWithRedirect } = useAuth0();
  const { authFetch } = useAuthFetch();

  useEffect(() => {
    const fetchSubjects = async () => {
      if (hasFetched.current) return;
      hasFetched.current = true;

      setLoading(true);
      try {
        //   // 1️⃣ Get the access token from Auth0
        //   let token: string;
        //   try {
        //     token = await getAccessTokenSilently({
        //       authorizationParams: {
        //         audience: import.meta.env.VITE_AUTH0_AUDIENCE,
        //       },
        //     });
        //     console.log("Access Token:", token);
        //   } catch (err: unknown) {
        //     // Handle consent/login requirement
        //     if (
        //       (typeof err === "object" &&
        //         err !== null &&
        //         "error" in err &&
        //         (err as { error: string }).error === "consent_required") ||
        //       (err as { error: string }).error === "login_required"
        //     ) {
        //       await loginWithRedirect({
        //         authorizationParams: {
        //           audience: import.meta.env.VITE_AUTH0_AUDIENCE,
        //         },
        //       });
        //       return; // stop fetch, user is redirected
        //     } else {
        //       throw err; // rethrow unknown errors
        //     }
        //   }

        // 2️⃣ Fetch subjects with Authorization header

        const response = await authFetch(`${API_URL}/subjects`);

        // const response = await fetch(`${API_URL}/subjects`, {
        //   headers: {
        //     Authorization: `Bearer ${token}`,
        //     "Content-Type": "application/json",
        //   },
        // });

        if (!response.ok) {
          throw new Error(`Failed to fetch subjects: ${response.status}`);
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
  }, [getAccessTokenSilently, loginWithRedirect, authFetch]); // include loginWithRedirect as dependency

  return { subjects, selectedSubject, loading, error };
};

export default useAllSubjects;
