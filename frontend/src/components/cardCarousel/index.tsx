import { useState, useEffect, useMemo } from "react";
import FlipCard from "../flipCard";
import type { Card } from "../../types/card";
import { useReviseSettings } from "../../hooks/reviseSettings";

interface CardCarouselProps {
  cards: Card[];
  currentIndex: number;
  setCurrentIndex?: (index: number) => void; // optional callback to update parent
  cardColors?: Record<string, string>;
}

const CardCarousel = ({
  cards,
  currentIndex,
  setCurrentIndex,
  cardColors = {},
}: CardCarouselProps) => {
  const [flippedMap, setFlippedMap] = useState<Record<string, boolean>>({});
  const [windowWidth, setWindowWidth] = useState(
    typeof window !== "undefined" ? window.innerWidth : 1024
  );

  const { cardDisplay, showDeckNames, familiarity } = useReviseSettings();

  // Memoize filtered cards
  const filteredCards = useMemo(() => {
    return cards.filter((card) => {
      if (familiarity === "All") return true;
      if (familiarity === "Hard") return card.avgRating >= 4;
      if (familiarity === "Easy") return card.avgRating < 4;
      return true;
    });
  }, [cards, familiarity]);

  // Adjust currentIndex if the filtered cards remove the current card
  useEffect(() => {
    if (currentIndex >= filteredCards.length && filteredCards.length > 0) {
      const newIndex = filteredCards.length - 1;
      setCurrentIndex?.(newIndex);
    }
  }, [filteredCards, currentIndex, setCurrentIndex]);

  // Initialize flippedMap
  useEffect(() => {
    setFlippedMap((prev) => {
      const newMap: Record<string, boolean> = { ...prev };
      filteredCards.forEach((card) => {
        if (cardDisplay === "Front") newMap[card.id] = false;
        else if (cardDisplay === "Back") newMap[card.id] = true;
        else if (cardDisplay === "Any" && newMap[card.id] === undefined) {
          newMap[card.id] = Math.random() < 0.5;
        }
      });
      return newMap;
    });
  }, [filteredCards, cardDisplay]);

  // Window resize
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

  const index =
    filteredCards.length > 0 ? currentIndex % filteredCards.length : 0;
  const translateX = -(index * fullCardWidth) + (windowWidth - cardWidth) / 2;

  return (
    <div className="relative w-full py-6">
      <div
        className="flex transition-transform duration-500"
        style={{ transform: `translateX(${translateX}px)` }}
      >
        {filteredCards.map((card, i) => {
          const distance = Math.abs(i - index);
          let scale = i === index ? 1 : 0.9;
          if (i !== index && windowWidth < 640) scale = 0.8;
          const translateY = i === index ? 0 : distance * 8;
          const flipped = flippedMap[card.id] ?? false;

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
