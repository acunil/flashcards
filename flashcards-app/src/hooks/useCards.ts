import { useEffect, useState } from "react";

export type Card = {
  id: string;
  front: string;
  back: string;
};

const BASE_URL = "http://localhost:8080/api/cards";

const useCards = (hardMode: boolean = false) => {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCards = async () => {
      try {
        setLoading(true);

        const url = hardMode
          ? `${BASE_URL}/minAvgRating?threshold=4`
          : BASE_URL;
        const response = await fetch(url);
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
  }, [hardMode]);

  return { cards, loading, error };
};

export default useCards;
