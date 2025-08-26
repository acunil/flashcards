import { useState, useCallback } from "react";
import { API_URL } from "../urls";
import type { UserStats } from "../../types/user-stats";
import { useAuthFetch } from "../../utils/authFetch";

const useUserStats = (userId: string) => {
  const [userStats, setUserStats] = useState<UserStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { authFetch } = useAuthFetch();

  const getUserStats = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data: UserStats | undefined = await authFetch(
        `${API_URL}/user-stats?userId=${userId}`
      );

      if (!data) {
        // User was likely redirected to login
        setUserStats(null);
        return;
      }

      setUserStats(data);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Unknown error occurred");
    } finally {
      setLoading(false);
    }
  }, [userId, authFetch]);

  return { userStats, loading, error, getUserStats };
};

export default useUserStats;
