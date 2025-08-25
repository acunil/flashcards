import { useState, useCallback } from "react";
import { API_URL } from "../urls";
import type { UserStats } from "../../types/user-stats";

const useUserStats = (userId: string) => {
  const [userStats, setUserStats] = useState<UserStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const getUserStats = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await fetch(`${API_URL}/user-stats?userId=${userId}`);
      if (!response.ok) {
        throw new Error("Failed to fetch user stats");
      }

      const data: UserStats = await response.json();
      setUserStats(data);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Unknown error occurred");
    } finally {
      setLoading(false);
    }
  }, [userId]);

  return { userStats, loading, error, getUserStats };
};

export default useUserStats;
