import { createContext, useContext } from "react";
import type { Deck } from "../types/deck";
import type { Card } from "../types/card";

export type AppContextType = {
  decks: Deck[];
  cards: Card[];
  loading: boolean;
  error: string | null;
  fetchDecks: () => Promise<void>;
  setDecks: React.Dispatch<React.SetStateAction<Deck[]>>;
};

export const AppContext = createContext<AppContextType | undefined>(undefined);

export const useAppContext = () => {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error("useAppContext must be used inside AppProvider");
  return ctx;
};
