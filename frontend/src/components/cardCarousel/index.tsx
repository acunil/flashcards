import { useState, useEffect } from "react";
import FlipCard from "../flipCard";
import type { Card } from "../../types/card";
import { useReviseSettings } from "../../hooks/reviseSettings";

interface CardCarouselProps {
  cards: Card[];
  currentIndex: number;
  cardColors?: Record<string, string>;
}

const CardCarousel = ({
  cards,
  currentIndex,
  cardColors = {},
}: CardCarouselProps) => {
  const [flippedMap, setFlippedMap] = useState<Record<string, boolean>>({});
  const [windowWidth, setWindowWidth] = useState(
    typeof window !== "undefined" ? window.innerWidth : 1024
  );

  const { cardDisplay, showDeckNames } = useReviseSettings();

  // Initialize flippedMap whenever cardDisplay or cards change
  useEffect(() => {
    if (cardDisplay === "Any") {
      // Randomize all cards for "Any"
      setFlippedMap(() => {
        const newMap: Record<string, boolean> = {};
        cards.forEach((card) => {
          newMap[card.id] = Math.random() < 0.5;
        });
        return newMap;
      });
    } else {
      // For Front or Back, set all cards accordingly
      setFlippedMap(() => {
        const newMap: Record<string, boolean> = {};
        cards.forEach((card) => {
          newMap[card.id] = cardDisplay === "Back";
        });
        return newMap;
      });
    }
  }, [cards, cardDisplay]);

  // Update window width for responsive sizing
  useEffect(() => {
    const handleResize = () => setWindowWidth(window.innerWidth);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const maxCardWidth = 320;
  const cardMargin = 8;
  const cardWidth = Math.min(maxCardWidth, windowWidth - 2 * cardMargin);
  const fullCardWidth = cardWidth + cardMargin;

  const handleFlip = (cardId: number) => {
    setFlippedMap((prev) => ({
      ...prev,
      [cardId]: !prev[cardId],
    }));
  };

  const translateX =
    -(currentIndex * fullCardWidth) + (windowWidth - cardWidth) / 2;

  return (
    <div className="relative w-full py-6">
      <div
        className="flex transition-transform duration-500"
        style={{ transform: `translateX(${translateX}px)` }}
      >
        {cards.map((card, i) => {
          const distance = Math.abs(i - currentIndex);
          const flipped = flippedMap[card.id] ?? false; // use state only

          let scale = i === currentIndex ? 1 : 0.9;
          if (i !== currentIndex && windowWidth < 640) scale = 0.8;
          const translateY = i === currentIndex ? 0 : distance * 8;

          return (
            <div
              key={card.id}
              className="flex-shrink-0"
              style={{
                width: cardWidth,
                margin: `0 ${cardMargin / 2}px`,
                transform: `scale(${scale}) translateY(${translateY}px)`,
                transition: "transform 0.5s ease",
                cursor: i === currentIndex ? "pointer" : "default",
                zIndex: i === currentIndex ? 10 : 1,
              }}
            >
              <FlipCard
                card={card}
                flipped={flipped}
                onFlip={() => handleFlip(card.id)}
                showDecks={showDeckNames === "Show"}
                cardBgColor={cardColors[card.id] || "bg-white"}
              />
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default CardCarousel;
