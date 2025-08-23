import { useEffect, useState } from "react";
import { AppContext } from "./AppContext";
import { useAllDecks } from "../hooks/decks";
import type { Deck } from "../types/deck";
import useCards from "../hooks/cards/useCards";
import type { Card } from "../types/card";
import useSubjects from "../hooks/subjects/useSubjects";

export const AppProvider = ({ children }: { children: React.ReactNode }) => {
  const { decks, fetchDecks } = useAllDecks();
  const { cards, loading, error } = useCards();
  const { subjects } = useSubjects();
  const [localDecks, setDecks] = useState<Deck[]>(decks);
  const [localCards, setCards] = useState<Card[]>(cards);

  console.log(subjects);

  useEffect(() => {
    setDecks(decks);
  }, [decks]);

  useEffect(() => {
    setCards(cards);
  }, [cards]);

  return (
    <AppContext.Provider
      value={{
        decks: localDecks,
        cards: localCards,
        loading,
        error,
        fetchDecks,
        setDecks,
        setCards,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};
