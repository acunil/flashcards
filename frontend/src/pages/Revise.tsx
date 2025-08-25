import { useEffect, useMemo, useState } from "react";
import DifficultyButtons from "../components/difficultyButtons";
import Header from "../components/header";
import useRateCard from "../hooks/cards/useRateCard";
import CardCarousel from "../components/cardCarousel";
import { levels } from "../components/difficultyButtons/levels";
import type { Deck } from "../types/deck";
import { useAppContext } from "../contexts";
import type { Card } from "../types/card";
import ReviseButtons from "../components/reviseButtons";
import { useNavigate } from "react-router-dom";

interface ReviseProps {
  hardMode?: boolean;
  deckId?: number;
}

const Revise = ({ hardMode = false, deckId }: ReviseProps) => {
  const { rateCard } = useRateCard();
  const { cards, loading, error } = useAppContext();
  const navigate = useNavigate();

  const [currentIndex, setCurrentIndex] = useState(0);
  const [showHint, setShowHint] = useState(false);

  useEffect(() => {
    setShowHint(false);
  }, [currentIndex]);

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

  const handleEditCard = () => {
    navigate(`/add-card/${revisionCards[currentIndex].id}`);
  };

  const handleShowHint = () => {
    setShowHint(true);
  };

  return (
    <div className={`min-h-screen ${hardMode ? "bg-pink-300" : "bg-pink-200"}`}>
      <Header isRevising={true} />
      <main className="flex flex-col items-center my-2">
        {loading && <p>Loading cards...</p>}
        {error && (
          <div className="bg-white w-full max-w-screen-sm border-black border-2 p-3 rounded m-4 text-center">
            <p>No cards found</p>
          </div>
        )}
        {!loading && !error && cards.length > 0 && (
          <>
            <ReviseButtons
              disableHint={
                // Disable if the card has no hint OR if the hint has already been shown
                !(
                  revisionCards[currentIndex].hintFront ||
                  revisionCards[currentIndex].hintBack
                ) || showHint
              }
              onEdit={handleEditCard}
              onShowHint={handleShowHint}
            />
            <div className="w-full overflow-hidden flex flex-col items-center">
              <CardCarousel
                cards={revisionCards}
                currentIndex={currentIndex}
                setCurrentIndex={setCurrentIndex}
                cardColors={cardColors}
                displayCurrentHint={showHint}
              />
              <DifficultyButtons onSelectDifficulty={handleDifficultySelect} />
            </div>
          </>
        )}
        {!loading && !error && cards.length === 0 && <p>No cards available.</p>}
      </main>
    </div>
  );
};

export default Revise;
