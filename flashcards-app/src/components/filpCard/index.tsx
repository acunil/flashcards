import { useState } from "react";

export interface Card {
  id: string;
  front: string;
  back: string;
  // add more fields if needed
}

interface FlipCardProps {
  cards: Card[];
}

const FlipCard = ({ cards }: FlipCardProps) => {
  const [flipped, setFlipped] = useState(false);
  const [currentIndex] = useState(0);

  if (cards.length === 0) {
    return (
      <div className="w-72 h-48 flex items-center justify-center">
        No cards to display
      </div>
    );
  }

  const currentCard = cards[currentIndex];

  return (
    <div
      className="w-72 h-48 perspective cursor-pointer"
      onClick={() => setFlipped((prev) => !prev)}
    >
      <div
        className={`relative w-full h-full transition-transform duration-500 transform-style preserve-3d ${
          flipped ? "rotate-y-180" : ""
        }`}
      >
        <div className="absolute w-full h-full flex items-center justify-center bg-white border rounded-xl backface-hidden p-4 text-center">
          {currentCard.front}
        </div>
        <div className="absolute w-full h-full flex items-center justify-center bg-gray-100 border rounded-xl rotate-y-180 backface-hidden p-4 text-center">
          {currentCard.back}
        </div>
      </div>
    </div>
  );
};

export default FlipCard;
