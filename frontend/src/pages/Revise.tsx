import { useCallback, useEffect, useState } from "react";
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
import PageLoad from "../components/pageLoad";

interface ReviseProps {
  hardMode?: boolean;
  deckId?: number;
}

const shuffleCards = (cards: Card[]) => {
  const copy = [...cards];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
};

const Revise = ({ hardMode = false, deckId }: ReviseProps) => {
  const { rateCard } = useRateCard();
  const { cards, loading, error } = useAppContext();
  const navigate = useNavigate();

  const [currentIndex, setCurrentIndex] = useState(0);
  const [showHint, setShowHint] = useState(false);
  const [revisionCards, setRevisionCards] = useState<Card[]>([]);
  const [cardColors, setCardColors] = useState<Record<string, string>>({});

  useEffect(() => {
    setShowHint(false);
  }, [currentIndex]);

  const buildRevisionCards = useCallback(() => {
    const deckFiltered = cards.filter(
      (card) => !deckId || card.decks.some((deck: Deck) => deck.id === deckId)
    );

    if (hardMode) {
      const hardFiltered = deckFiltered.filter((card) => card.avgRating >= 4);
      return shuffleCards(
        hardFiltered.length > 0 ? hardFiltered : deckFiltered
      );
    }

    return shuffleCards(deckFiltered);
  }, [cards, deckId, hardMode]);

  useEffect(() => {
    const newDeck = buildRevisionCards();
    setRevisionCards(newDeck);
    setCurrentIndex(0);
    setCardColors({});
  }, [buildRevisionCards]);

  const handleDifficultySelect = (rating: number) => {
    if (revisionCards.length === 0) return;

    const currentCard = revisionCards[currentIndex];
    rateCard(currentCard.id, rating);

    const level = levels.find((l) => l.rating === rating);
    const newColor = level ? level.buttonClassName : "bg-white";

    setCardColors((prevColors) => ({
      ...prevColors,
      [currentCard.id]: newColor,
    }));

    // 🔄 Loop through cards instead of resetting
    setCurrentIndex((prev) => (prev + 1) % revisionCards.length);
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
        {loading && <PageLoad />}
        {error && (
          <div className="bg-white w-full max-w-screen-sm border-black border-2 p-3 rounded m-4 text-center">
            <p>No cards found</p>
          </div>
        )}
        {!loading && !error && revisionCards.length === 0 && (
          <div className="bg-white w-full max-w-screen-sm border-black border-2 p-3 rounded m-4 text-center">
            <p>You don't have any cards!</p>
            <p>Create a card to start revising</p>
            <button
              className="cursor-pointer border-2 m-2 border-black p-2 rounded bg-yellow-200"
              onClick={() => navigate("/add-card/")}
            >
              Add a card
            </button>
          </div>
        )}
        {!loading && !error && revisionCards.length > 0 && (
          <>
            <ReviseButtons
              disableHint={
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
      </main>
    </div>
  );
};

export default Revise;
