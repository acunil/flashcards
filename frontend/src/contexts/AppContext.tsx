import { createContext, useContext } from "react";
import type { Deck } from "../types/deck";
import type { Card } from "../types/card";
import type { Subject } from "../types/subject";

export type AppContextType = {
  decks: Deck[];
  cards: Card[];
  loading: boolean;
  error: string | null;
  subjects: Subject[];
  setSubjects: React.Dispatch<React.SetStateAction<Subject[]>>;
  fetchDecks: () => Promise<void>;
  setDecks: React.Dispatch<React.SetStateAction<Deck[]>>;
  setCards: React.Dispatch<React.SetStateAction<Card[]>>;
  selectedSubjectId: number | null;
  setSelectedSubjectId: React.Dispatch<React.SetStateAction<number | null>>;
  selectedSubject: Subject | null;
};

export const AppContext = createContext<AppContextType | undefined>(undefined);

export const useAppContext = () => {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error("useAppContext must be used inside AppProvider");
  return ctx;
};
