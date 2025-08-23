import { createContext } from "react";

// Types for settings
export type CardDisplay = "Front" | "Back" | "Any";
export type Familiarity = "All" | "Hard" | "Easy";
export type DeckVisibility = "Show" | "Hide";

// Interface for context value
export interface ReviseSettings {
  cardDisplay: CardDisplay;
  setCardDisplay: (val: CardDisplay) => void;
  familiarity: Familiarity;
  setFamiliarity: (val: Familiarity) => void;
  showDeckNames: DeckVisibility;
  setShowDeckNames: (val: DeckVisibility) => void;
}

// Create context with undefined as default
export const ReviseSettingsContext = createContext<ReviseSettings | undefined>(
  undefined
);
