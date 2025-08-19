import { useState } from "react";
import FlipCard from "../flipCard";
import type { Card } from "../../types/card";

interface CardCarouselProps {
  cards: Card[];
  showDecks?: boolean;
  currentIndex: number;
  setCurrentIndex: (index: number) => void;
  cardColors?: Record<string, string>;
  cardDisplay?: "Front" | "Back" | "Any";
}

const CardCarousel = ({
  cards,
  showDecks = false,
  currentIndex,
  cardColors = {},
  cardDisplay = "Front",
}: CardCarouselProps) => {
  const [flippedMap, setFlippedMap] = useState<Record<string, boolean>>({});

  const cardWidth = 320;
  const cardMargin = 16;
  const previewWidth = 60;

  const handleFlip = () => {
    const currentCardId = cards[currentIndex].id;
    setFlippedMap((prev) => ({
      ...prev,
      [currentCardId]: !prev[currentCardId],
    }));
  };

  const fullCardWidth = cardWidth + cardMargin;
  const visibleWidth = fullCardWidth + previewWidth * 2;
  const translateX =
    -currentIndex * fullCardWidth + previewWidth + cardMargin / 2;

  return (
    <div
      className="relative mx-auto overflow-visible py-6"
      style={{ width: visibleWidth, height: 220 }}
    >
      <div
        className="flex transition-transform duration-500"
        style={{
          width: cards.length * fullCardWidth,
          transform: `translateX(${translateX}px)`,
        }}
      >
        {cards.map((card, i) => {
          const isCurrent = i === currentIndex;
          const flipped = flippedMap[card.id] || false;

          // Calculate distance from center
          const distance = Math.abs(i - currentIndex);

          // Determine scale factor based on distance
          // You can tweak these numbers as you like
          let scale = 1 - distance * 0.15;
          if (scale < 0.7) scale = 0.7; // minimum scale

          // Optional: Slight vertical shift to add a 3D effect
          const translateY = distance * 10; // pixels down for farther cards

          // Logic for flip state and displayMode for FlipCard
          let flipState = false;
          let displayMode: "Front" | "Back" | "Any" = "Front";

          if (cardDisplay === "Front") {
            flipState = isCurrent ? flipped : false;
            displayMode = "Front";
          } else if (cardDisplay === "Back") {
            flipState = true;
            displayMode = "Back";
          } else if (cardDisplay === "Any") {
            flipState = flipped;
            displayMode = "Any";
          }

          return (
            <div
              key={card.id}
              className="flex-shrink-0"
              style={{
                width: cardWidth,
                margin: "0 8px",
                transform: `scale(${scale}) translateY(${translateY}px)`,
                transition: "transform 0.5s ease",
                cursor: isCurrent ? "pointer" : "default",
                zIndex: isCurrent ? 10 : 1, // keep center card on top
              }}
            >
              <FlipCard
                card={card}
                flipped={flipState}
                onFlip={isCurrent ? handleFlip : undefined}
                showDecks={showDecks}
                cardBgColor={cardColors[card.id] || "bg-white"}
                displayMode={displayMode}
              />
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default CardCarousel;
