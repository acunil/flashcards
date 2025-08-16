// src/contexts/DeckContext.tsx
import { createContext, useContext } from "react";
import type { Deck } from "../types/deck";

export type DeckContextType = {
  decks: Deck[];
  loading: boolean;
  error: string | null;
  fetchDecks: () => Promise<void>;
  setDecks: React.Dispatch<React.SetStateAction<Deck[]>>;
};

export const DeckContext = createContext<DeckContextType | undefined>(
  undefined
);

export const useDeckContext = () => {
  const ctx = useContext(DeckContext);
  if (!ctx) throw new Error("useDeckContext must be used inside DeckProvider");
  return ctx;
};
