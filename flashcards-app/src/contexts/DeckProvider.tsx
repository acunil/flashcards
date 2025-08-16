// src/contexts/DeckProvider.tsx
import { useState } from "react";
import { DeckContext } from "./DeckContext";
import { useAllDecks } from "../hooks/decks";
import type { Deck } from "../types/deck";

export const DeckProvider = ({ children }: { children: React.ReactNode }) => {
  const { loading, error, fetchDecks } = useAllDecks();

  const mockDecks: Deck[] = [
    {
      id: "deck1",
      name: "Math Basics",
      cardResponses: [
        {
          id: "card1",
          front: "2 + 2",
          back: "4",
          deckNames: ["Math Basics"],
          avgRating: 3,
          viewCount: 10,
          lastViewed: "2025-08-15T12:00:00Z",
          lastRating: 3,
        },
        {
          id: "card2",
          front: "3 x 3",
          back: "9",
          deckNames: ["Math Basics"],
          avgRating: 5,
          viewCount: 7,
          lastViewed: "2025-08-14T12:00:00Z",
          lastRating: 5,
        },
      ],
    },
    {
      id: "deck2",
      name: "Science",
      cardResponses: [
        {
          id: "card3",
          front: "H2O is?",
          back: "Water",
          deckNames: ["Science"],
          avgRating: 4,
          viewCount: 12,
          lastViewed: "2025-08-13T12:00:00Z",
          lastRating: 4,
        },
      ],
    },
  ];

  const [localDecks, setDecks] = useState<Deck[]>(mockDecks);

  // useEffect(() => {
  //   setDecks(decks);
  // }, [decks]);

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
