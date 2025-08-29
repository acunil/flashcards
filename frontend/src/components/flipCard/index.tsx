import type { Card } from "../../types/card";
import { useAppContext } from "../../contexts";

interface FlipCardProps {
  card: Card;
  flipped: boolean;
  onFlip?: () => void;
  displayMode?: "Front" | "Back" | "Any";
  cardBgColor?: string;
  showDecks?: boolean;
  showHint?: boolean;
  isActive: boolean;
  customSizeClassName?: string;
}

const FlipCard = ({
  card,
  flipped,
  onFlip,
  showDecks = false,
  cardBgColor = "bg-white",
  showHint = false,
  isActive = false,
  customSizeClassName,
}: FlipCardProps) => {
  const { selectedSubject } = useAppContext();
  return (
    <div
      className={`relative ${
        customSizeClassName ? customSizeClassName : "w-full h-full"
      }  ${isActive ? "cursor-pointer" : "cursor-default"}`}
      onClick={isActive ? onFlip : undefined}
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
          <div
            className={`absolute w-full h-full [backface-visibility:hidden] border-2 rounded-xl shadow p-4 ${cardBgColor}`}
          >
            <span className="absolute top-2 left-2 text-xs text-gray-400">
              {selectedSubject?.frontLabel || "front"}
            </span>
            <div className="flex items-center justify-center h-full text-center px-2">
              <div className="flex flex-col gap-2">
                <p>{card.front}</p>
                {card.hintBack && showHint && (
                  <p className="text-gray-500 font-light">{card.hintFront}</p>
                )}
              </div>
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
              <div className="flex flex-col gap-2">
                <p>{card.back}</p>
                {card.hintBack && showHint && (
                  <p className="text-gray-500 font-light">{card.hintBack}</p>
                )}
              </div>
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
