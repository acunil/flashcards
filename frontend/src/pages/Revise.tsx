import { useEffect, useState } from "react";
import DifficultyButtons from "../components/difficultyButtons";
import Header from "../components/header";
import useRateCard from "../hooks/cards/useRateCard";
import CardCarousel from "../components/cardCarousel";
import { levels } from "../components/difficultyButtons/levels";
import type { Deck } from "../types/deck";
import { useAppContext } from "../contexts";
import type { Card } from "../types/card";
import ReviseButtons from "../components/reviseButtons";
import { useLocation, useNavigate } from "react-router-dom";
import PageLoad from "../components/pageLoad";
import PageWrapper from "../components/pageWrapper";
import ContentWrapper from "../components/contentWrapper";
import { useReviseSettings } from "../hooks/reviseSettings";

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
  const { cards, loading, error } = useAppContext();
  const { cardOrder } = useReviseSettings();
  const { rateCard } = useRateCard();
  const navigate = useNavigate();
  const location = useLocation();

  const frontCardIdFromState = location.state?.frontCardId;

  const [revisionCards, setRevisionCards] = useState<Card[] | null>(null); // null until ready
  const [currentIndex, setCurrentIndex] = useState(0);
  const [showHint, setShowHint] = useState(false);
  const [cardColors, setCardColors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (!loading) {
      let deckFiltered = cards.filter(
        (card) => !deckId || card.decks.some((deck: Deck) => deck.id === deckId)
      );

      if (hardMode) {
        const hardFiltered = deckFiltered.filter((card) => card.avgRating >= 4);
        deckFiltered = hardFiltered.length > 0 ? hardFiltered : deckFiltered;
      }

      let ordered: Card[];

      if (cardOrder === "Shuffle") {
        ordered = shuffleCards(deckFiltered);
      } else if (cardOrder === "Newest") {
        ordered = [...deckFiltered].reverse();
      } else {
        ordered = [...deckFiltered];
      }

      if (frontCardIdFromState) {
        const index = ordered.findIndex((c) => c.id === frontCardIdFromState);
        if (index > -1) {
          const [frontCard] = ordered.splice(index, 1);
          ordered = [frontCard, ...ordered];
        }
      }

      setRevisionCards(ordered);
      setCurrentIndex(0);
      setCardColors({});
      setShowHint(false);
    }
  }, [loading, cards, deckId, hardMode, frontCardIdFromState, cardOrder]);

  // Until revisionCards is ready, show loader
  if (revisionCards === null) return <PageLoad />;

  const handleDifficultySelect = (rating: number) => {
    if (!revisionCards.length) return;
    const currentCard = revisionCards[currentIndex];
    rateCard(currentCard.id, rating);

    const level = levels.find((l) => l.rating === rating);
    const newColor = level ? level.buttonClassName : "bg-white";

    setCardColors((prev) => ({ ...prev, [currentCard.id]: newColor }));
    setCurrentIndex((prev) => (prev + 1) % revisionCards.length);
    setShowHint(false);
  };

  const handleEditCard = () => {
    navigate(`/add-card/${revisionCards[currentIndex].id}`, {
      state: {
        returnToRevise: true,
        cardId: revisionCards[currentIndex].id,
        deckId,
        hardMode,
      },
    });
  };

  const toggleHint = () => {
    const currentCard = revisionCards[currentIndex];
    if (currentCard.hintFront || currentCard.hintBack)
      setShowHint((prev) => !prev);
  };

  return (
    <PageWrapper className={`${hardMode ? "bg-pink-300" : "bg-pink-200"}`}>
      <Header isRevising />
      <main className="flex flex-col items-center my-2">
        {error && (
          <ContentWrapper>
            <div className="bg-white w-full max-w-screen-sm border-black border-2 p-3 rounded m-4 text-center">
              <p>No cards found</p>
            </div>
          </ContentWrapper>
        )}

        {!error && revisionCards.length === 0 && (
          <ContentWrapper>
            <div className="flex flex-col items-center">
              <p>You don't have any cards!</p>
              <p>Add a card to start revising</p>
              <button
                className="cursor-pointer border-2 m-2 border-black p-2 rounded bg-yellow-200"
                onClick={() => navigate("/add-card/")}
              >
                Add a card
              </button>
              <button
                className="cursor-pointer border-2 border-black p-2 rounded bg-green-200"
                onClick={() => navigate("/upload/")}
              >
                Bulk upload
              </button>
            </div>
          </ContentWrapper>
        )}

        {revisionCards.length > 0 && (
          <>
            <div className="py-2 right-2 relative">
              <ReviseButtons
                showHintButton={
                  !!(
                    revisionCards[currentIndex].hintFront ||
                    revisionCards[currentIndex].hintBack
                  )
                }
                onEdit={handleEditCard}
                onShowHint={toggleHint}
              />
            </div>
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
    </PageWrapper>
  );
};

export default Revise;
