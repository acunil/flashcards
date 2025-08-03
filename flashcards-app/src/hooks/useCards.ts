import { useEffect, useState } from "react";

export type Card = {
  id: string;
  front: string;
  back: string;
};

const url = "http://localhost:8080/api/cards";

const useCards = () => {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCards = async () => {
      try {
        setLoading(true);
        const response = await fetch(url);
        console.log(response);
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
  }, []);

  return { cards, loading, error };
};

export default useCards;
