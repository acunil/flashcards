import { useMemo, useState } from "react";
import DifficultyButtons from "../components/difficultyButtons";
import Header from "../components/header";
import useRateCard from "../hooks/cards/useRateCard";
import CardCarousel from "../components/cardCarousel";
import { levels } from "../components/difficultyButtons/levels";
import type { Deck } from "../types/deck";
import { useAppContext } from "../contexts";
import type { Card } from "../types/card";

interface ReviseProps {
  hardMode?: boolean;
  deckId?: number;
}

const Revise = ({ hardMode = false, deckId }: ReviseProps) => {
  const { rateCard } = useRateCard();
  const { cards, loading, error } = useAppContext();

  const [currentIndex, setCurrentIndex] = useState(0);
  const [cardDisplay, setCardDisplay] = useState<"Front" | "Back" | "Any">(
    "Front"
  );

  // filter all cards for the revision deck
  const revisionCards: Card[] = useMemo(() => {
    const filtered = cards.filter(
      (card) =>
        (!deckId || card.decks.some((deck: Deck) => deck.id === deckId)) &&
        (!hardMode || card.avgRating >= 4)
    );

    // Shuffle using Fisher–Yates algorithm
    for (let i = filtered.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [filtered[i], filtered[j]] = [filtered[j], filtered[i]];
    }

    return filtered;
  }, [cards, deckId, hardMode]);

  // Map card id -> color
  const [cardColors, setCardColors] = useState<Record<string, string>>({});

  const handleDifficultySelect = (rating: number) => {
    console.log("handle difficulty select");
    if (revisionCards.length === 0) return;

    const currentCard = revisionCards[currentIndex];
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
      <main className="flex flex-col items-center">
        {loading && <p>Loading cards...</p>}
        {error && <p className="text-red-600">{error}</p>}
        {!loading && !error && cards.length > 0 && (
          <div className="w-full overflow-hidden flex flex-col items-center">
            <CardCarousel
              cards={revisionCards}
              showDecks={false}
              currentIndex={currentIndex}
              setCurrentIndex={setCurrentIndex}
              cardColors={cardColors}
            />
            <DifficultyButtons onSelectDifficulty={handleDifficultySelect} />
          </div>
        )}
        {!loading && !error && cards.length === 0 && <p>No cards available.</p>}
      </main>
    </div>
  );
};

export default Revise;
