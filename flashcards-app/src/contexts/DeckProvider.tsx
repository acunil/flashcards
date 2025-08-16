import { useEffect, useState } from "react";
import { DeckContext } from "./DeckContext";
import { useAllDecks } from "../hooks/decks";
import type { Deck } from "../types/deck";

export const DeckProvider = ({ children }: { children: React.ReactNode }) => {
  const { decks, loading, error, fetchDecks } = useAllDecks();
  const [localDecks, setDecks] = useState<Deck[]>(decks);

  useEffect(() => {
    setDecks(decks);
  }, [decks]);

  return (
    <DeckContext.Provider
      value={{
        decks: localDecks,
        loading,
        error,
        fetchDecks,
        setDecks,
      }}
    >
      {children}
    </DeckContext.Provider>
  );
};
