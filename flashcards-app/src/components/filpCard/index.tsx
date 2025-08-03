import { useEffect, useState } from "react";

export interface Card {
  id: string;
  front: string;
  back: string;
}

interface FlipCardProps {
  card: Card;
  displayMode?: "Front" | "Back" | "Any";
  sideLabelFront?: string;
  sideLabelBack?: string;
  decks?: string;
  showDecks?: boolean;
}

const FlipCard = ({
  card,
  displayMode = "Front",
  sideLabelFront = "front",
  sideLabelBack = "back",
  showDecks = false,
  decks,
}: FlipCardProps) => {
  const [flipped, setFlipped] = useState(false);
  const [internalCard, setInternalCard] = useState(card);

  useEffect(() => {
    if (card.id !== internalCard.id) {
      let defaultFlipped = false;

      if (displayMode === "Back") defaultFlipped = true;
      if (displayMode === "Any") defaultFlipped = Math.random() < 0.5;
      // Start flip reset
      setFlipped(defaultFlipped);

      // Wait for flip reset animation to finish before updating content
      const timeout = setTimeout(() => {
        setInternalCard(card);
      }, 500); // match transition duration (500ms)

      return () => clearTimeout(timeout);
    }
  }, [card, displayMode, internalCard.id]);

  return (
    <div
      className="w-80 h-52 cursor-pointer"
      onClick={() => setFlipped((prev) => !prev)}
    >
      <div className="relative w-full h-full [perspective:1000px]">
        <div
          className={`relative w-full h-full transition-transform duration-500 [transform-style:preserve-3d] ${
            flipped
              ? "[transform:rotateY(180deg)]"
              : "[transform:rotateY(0deg)]"
          }`}
        >
          {/* Front Side */}
          <div className="absolute w-full h-full [backface-visibility:hidden] bg-white border-2 rounded-xl shadow p-4">
            <span className="absolute top-2 left-2 text-xs text-gray-400">
              {sideLabelFront}
            </span>
            <div className="flex items-center justify-center h-full text-center px-2">
              {internalCard.front}
            </div>
            {showDecks && decks && (
              <span className="absolute bottom-2 right-2 text-xs text-gray-400">
                {decks}
              </span>
            )}
          </div>

          {/* Back Side */}
          <div className="absolute w-full h-full [backface-visibility:hidden] bg-gray-100 border-2 rounded-xl shadow p-4 [transform:rotateY(180deg)]">
            <span className="absolute top-2 left-2 text-xs text-gray-400">
              {sideLabelBack}
            </span>
            <div className="flex items-center justify-center h-full text-center px-2">
              {internalCard.back}
            </div>
            {showDecks && decks && (
              <span className="absolute bottom-2 right-2 text-xs text-gray-400">
                {decks}
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default FlipCard;
