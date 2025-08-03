import { useState } from "react";

export interface Card {
  id: string;
  front: string;
  back: string;
}

interface FlipCardProps {
  card: Card;
}
const FlipCard = ({ card }: FlipCardProps) => {
  const [flipped, setFlipped] = useState(false);

  return (
      <div
          className="w-72 h-48 perspective cursor-pointer"
          onClick={() => setFlipped((prev) => !prev)}
      >
        <div
            className={`flip-card-inner ${flipped ? "rotate-y-180" : ""}`}
            style={{ transform: flipped ? "rotateY(180deg)" : "rotateY(0deg)" }}
        >
          <div className="flip-card-front bg-white border">
            {card.front}
          </div>
          <div className="flip-card-back bg-gray-100 border">
            {card.back}
          </div>
        </div>
      </div>
  );
};


export default FlipCard;
