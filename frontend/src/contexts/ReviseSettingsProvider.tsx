import { useState, type ReactNode } from "react";
import {
  ReviseSettingsContext,
  type CardDisplay,
  type Familiarity,
  type DeckVisibility,
} from "./ReviseSettingsContext";

// Provider component
export const ReviseSettingsProvider = ({
  children,
}: {
  children: ReactNode;
}) => {
  const [cardDisplay, setCardDisplay] = useState<CardDisplay>("Front");
  const [familiarity, setFamiliarity] = useState<Familiarity>("All");
  const [showDeckNames, setShowDeckNames] = useState<DeckVisibility>("Show");

  return (
    <ReviseSettingsContext.Provider
      value={{
        cardDisplay,
        setCardDisplay,
        familiarity,
        setFamiliarity,
        showDeckNames,
        setShowDeckNames,
      }}
    >
      {children}
    </ReviseSettingsContext.Provider>
  );
};
