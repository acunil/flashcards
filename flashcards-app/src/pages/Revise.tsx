import { useMemo, useState } from "react";
import DifficultyButtons from "../components/difficultyButtons";
import Header from "../components/header";
import useRateCard from "../hooks/cards/useRateCard";
import CardCarousel from "../components/cardCarousel";
import { levels } from "../components/difficultyButtons/levels";
import { useDeckContext } from "../contexts";
import type { CardResponse } from "../types/cardResponse";
import type { Deck } from "../types/deck";

interface ReviseProps {
  hardMode?: boolean;
  deckId?: string;
}

const Revise = ({ hardMode = false, deckId }: ReviseProps) => {
  const { rateCard } = useRateCard();
  const { decks, loading, error } = useDeckContext();

  const [currentIndex, setCurrentIndex] = useState(0);
  const [cardDisplay, setCardDisplay] = useState<"Front" | "Back" | "Any">(
    "Front"
  );

  const cards: CardResponse[] = useMemo(() => {
    let allDecks: Deck[] = decks;

    if (deckId) {
      allDecks = decks.filter((d) => d.id === deckId);
    }

    const allCards = Array.from(
      new Map(
        allDecks
          .flatMap((deck) => deck.cardResponses)
          .map((card) => [card.id, card])
      ).values()
    );

    return hardMode ? allCards.filter((card) => card.avgRating >= 4) : allCards;
  }, [decks, deckId, hardMode]);

  // Map card id -> color
  const [cardColors, setCardColors] = useState<Record<string, string>>({});

  const handleDifficultySelect = (rating: number) => {
    if (cards.length === 0) return;

    const currentCard = cards[currentIndex];
    rateCard(currentCard.id, rating);

    const level = levels.find((l) => l.rating === rating);
    const newColor = level ? level.buttonClassName : "bg-white";

    // Set the color for the *current* card before moving forward
    setCardColors((prevColors) => ({
      ...prevColors,
      [currentCard.id]: newColor,
    }));

    // Then move to next card
    setCurrentIndex((prevIndex) =>
      prevIndex < cards.length - 1 ? prevIndex + 1 : 0
    );
  };

  return (
    <div className={`min-h-screen ${hardMode ? "bg-pink-300" : "bg-pink-200"}`}>
      <Header
        cardDisplay={cardDisplay}
        setCardDisplay={setCardDisplay}
        isRevising={true}
      />
      <main className="flex flex-col items-center gap-6 p-6">
        {loading && <p>Loading cards...</p>}
        {error && <p className="text-red-600">{error}</p>}
        {!loading && !error && cards.length > 0 && (
          <>
            <CardCarousel
              cards={cards}
              showDecks={false}
              currentIndex={currentIndex}
              setCurrentIndex={setCurrentIndex}
              cardColors={cardColors} // pass the colors map here
            />
            <DifficultyButtons onSelectDifficulty={handleDifficultySelect} />
          </>
        )}
        {!loading && !error && cards.length === 0 && <p>No cards available.</p>}
      </main>
    </div>
  );
};

export default Revise;
