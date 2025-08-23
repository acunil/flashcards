import type { Card } from "../../types/card";
import { useAppContext } from "../../contexts";

interface FlipCardProps {
  card: Card;
  flipped: boolean;
  onFlip?: () => void;
  displayMode?: "Front" | "Back" | "Any";
  cardBgColor?: string;
  showDecks?: boolean;
}

const FlipCard = ({
  card,
  flipped,
  onFlip,
  showDecks = false,
  cardBgColor = "bg-white",
}: FlipCardProps) => {
  const { selectedSubject } = useAppContext();

  const handleClick = () => {
    if (onFlip) onFlip();
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
            <span className="absolute top-2 left-2 text-xs text-gray-400">
              {selectedSubject?.frontLabel || "front"}
            </span>
            <div className="flex items-center justify-center h-full text-center px-2 ">
              {card.front}
            </div>
            {showDecks && card.decks.length > 0 && (
              <span className="absolute bottom-2 right-2 text-xs text-gray-400 ">
                {card.decks.map((d) => d.name).join(", ")}
              </span>
            )}
          </div>

          {/* Back Side */}
          <div
            className={`absolute w-full h-full [backface-visibility:hidden] border-2 rounded-xl shadow p-4 [transform:rotateY(180deg)] ${cardBgColor}`}
          >
            <span className="absolute top-2 left-2 text-xs text-gray-400 ">
              {selectedSubject?.backLabel || "back"}
            </span>
            <div className="flex items-center justify-center h-full text-center px-2 ">
              {card.back}
            </div>
            {showDecks && card.decks.length > 0 && (
              <span className="absolute bottom-2 right-2 text-xs text-gray-400 ">
                {card.decks.map((d) => d.name).join(", ")}
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default FlipCard;
