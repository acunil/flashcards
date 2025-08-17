import { useEffect, useState } from "react";
import type { Card } from "../../types/card";

interface FlipCardProps {
  card: Card;
  flipped?: boolean;
  onFlip?: () => void;
  displayMode?: "Front" | "Back" | "Any";
  sideLabelFront?: string;
  cardBgColor?: string;
  sideLabelBack?: string;
  decks?: string;
  showDecks?: boolean;
}

const FlipCard = ({
  card,
  flipped: flippedProp,
  onFlip,
  displayMode = "Front",
  sideLabelFront = "front",
  sideLabelBack = "back",
  showDecks = false,
  decks,
  cardBgColor = "bg-white",
}: FlipCardProps) => {
  const [internalFlipped, setInternalFlipped] = useState(false);
  const [internalCard, setInternalCard] = useState(card);

  const isControlled = flippedProp !== undefined;
  const flipped = isControlled ? flippedProp : internalFlipped;

  useEffect(() => {
    if (card.id !== internalCard.id) {
      let defaultFlipped = false;

      if (displayMode === "Back") defaultFlipped = true;
      if (displayMode === "Any") defaultFlipped = Math.random() < 0.5;

      if (!isControlled) {
        setInternalFlipped(defaultFlipped);
      }

      const timeout = setTimeout(() => {
        setInternalCard(card);
      }, 500); // match transition duration (500ms)

      return () => clearTimeout(timeout);
    }
  }, [card, displayMode, internalCard.id, isControlled]);

  const handleClick = () => {
    if (onFlip) {
      onFlip();
    } else {
      setInternalFlipped((prev) => !prev);
    }
  };

  return (
    <div className={`w-80 h-52 cursor-pointer`} onClick={handleClick}>
      <div className="relative w-full h-full [perspective:1000px]">
        <div
          className={`relative w-full h-full transition-transform duration-500 [transform-style:preserve-3d] ${
            flipped
              ? "[transform:rotateY(180deg)]"
              : "[transform:rotateY(0deg)]"
          }`}
        >
          {/* Front Side */}
          <div
            className={`absolute w-full h-full [backface-visibility:hidden] border-2 rounded-xl shadow p-4 ${cardBgColor}`}
          >
            <span className="absolute top-2 left-2 text-xs text-gray-400 select-none">
              {sideLabelFront}
            </span>
            <div className="flex items-center justify-center h-full text-center px-2 select-none">
              {internalCard.front}
            </div>
            {showDecks && decks && (
              <span className="absolute bottom-2 right-2 text-xs text-gray-400 select-none">
                {decks}
              </span>
            )}
          </div>

          {/* Back Side */}
          <div
            className={`absolute w-full h-full [backface-visibility:hidden] border-2 rounded-xl shadow p-4 [transform:rotateY(180deg)] ${cardBgColor}`}
          >
            <span className="absolute top-2 left-2 text-xs text-gray-400 select-none">
              {sideLabelBack}
            </span>
            <div className="flex items-center justify-center h-full text-center px-2 select-none">
              {internalCard.back}
            </div>
            {showDecks && decks && (
              <span className="absolute bottom-2 right-2 text-xs text-gray-400 select-none">
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
