import { useEffect, useRef, useState } from "react";
import type { Card } from "../../types/card";
import { API_URL } from "../urls";

const useCards = () => {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetched = useRef(false);

  useEffect(() => {
    const fetchCards = async () => {
      if (hasFetched.current) return;
      hasFetched.current = true;

      try {
        setLoading(true);
        const response = await fetch(`${API_URL}/cards?subjectId=1`);
        if (!response.ok) {
          throw new Error("Failed to fetch cards");
        }
        const data: Card[] = await response.json();
        setCards(data);
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

    fetchCards();
  });

  return { cards, loading, error };
};

export default useCards;
