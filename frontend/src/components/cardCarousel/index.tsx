import { useState, useEffect } from "react";
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
  // setCurrentIndex,
  cardColors = {},
  cardDisplay = "Front",
}: CardCarouselProps) => {
  const [flippedMap, setFlippedMap] = useState<Record<string, boolean>>({});
  const [windowWidth, setWindowWidth] = useState(
    typeof window !== "undefined" ? window.innerWidth : 1024
  );

  useEffect(() => {
    const handleResize = () => setWindowWidth(window.innerWidth);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  // Dynamic card size and spacing
  const maxCardWidth = 320;
  const cardMargin = 8; // Fixed smaller margin for better mobile fit
  const cardWidth = Math.min(maxCardWidth, windowWidth - 2 * cardMargin);
  const fullCardWidth = cardWidth + cardMargin;

  const handleFlip = () => {
    const currentCardId = cards[currentIndex].id;
    setFlippedMap((prev) => ({
      ...prev,
      [currentCardId]: !prev[currentCardId],
    }));
  };

  // Center the current card
  const translateX =
    -(currentIndex * fullCardWidth) + (windowWidth - cardWidth) / 2;

  return (
    <div className="relative w-full py-6">
      <div
        className="flex transition-transform duration-500"
        style={{
          transform: `translateX(${translateX}px)`,
        }}
      >
        {cards.map((card, i) => {
          const isCurrent = i === currentIndex;
          const flipped = flippedMap[card.id] || false;
          const distance = Math.abs(i - currentIndex);

          // Scale side cards down for 3D effect
          let scale = isCurrent ? 1 : 0.9;
          if (!isCurrent && windowWidth < 640) scale = 0.8; // Smaller scale on mobile for side cards

          // Slight vertical offset for depth
          const translateY = isCurrent ? 0 : distance * 8;

          // Flip card logic
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
                margin: `0 ${cardMargin / 2}px`,
                transform: `scale(${scale}) translateY(${translateY}px)`,
                transition: "transform 0.5s ease",
                cursor: isCurrent ? "pointer" : "default",
                zIndex: isCurrent ? 10 : 1,
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
