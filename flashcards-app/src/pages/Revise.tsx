import { useState } from "react";
import DifficultyButtons from "../components/difficultyButtons";
import FlipCard from "../components/filpCard";
import Header from "../components/header";
import useCards from "../hooks/useCards";
import useRateCard from "../hooks/useRateCard";

interface ReviseProps {
  hardMode?: boolean;
}

const Revise = ({ hardMode = false }: ReviseProps) => {
  const { cards, loading, error } = useCards(hardMode);
  const { rateCard } = useRateCard();
  const [currentIndex, setCurrentIndex] = useState(0);
  const [cardDisplay, setCardDisplay] = useState<"Front" | "Back" | "Any">(
    "Front"
  );

  const handleDifficultySelect = (rating: number) => {
    if (cards.length === 0) return;

    const currentCard = cards[currentIndex];
    rateCard(currentCard.id, rating);

    handleNext();
  };

  const handleNext = () => {
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
            <FlipCard card={cards[currentIndex]} displayMode={cardDisplay} />
            <DifficultyButtons onSelectDifficulty={handleDifficultySelect} />
          </>
        )}
        {!loading && !error && cards.length === 0 && <p>No cards available.</p>}
      </main>
    </div>
  );
};

export default Revise;
