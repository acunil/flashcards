import { useState, useEffect, useMemo } from "react";
import FlipCard from "../flipCard";
import type { Card } from "../../types/card";
import { useReviseSettings } from "../../hooks/reviseSettings";
import { CaretLeft, CaretRight } from "phosphor-react";

interface CardCarouselProps {
  cards: Card[];
  currentIndex: number;
  setCurrentIndex?: (index: number) => void; // optional callback to update parent
  cardColors?: Record<string, string>;
  displayCurrentHint: boolean;
}

const CardCarousel = ({
  cards,
  currentIndex,
  setCurrentIndex,
  cardColors = {},
  displayCurrentHint = false,
}: CardCarouselProps) => {
  const [windowWidth, setWindowWidth] = useState(
    typeof window !== "undefined" ? window.innerWidth : 1024
  );

  const { cardDisplay, showDeckNames, familiarity } = useReviseSettings();

  // Memoize filtered cards
  const filteredCards = useMemo(() => {
    if (familiarity === "All") return cards;

    let filtered: Card[] = [];
    if (familiarity === "Hard") {
      filtered = cards.filter((card) => card.avgRating >= 4);
    } else if (familiarity === "Easy") {
      filtered = cards.filter((card) => card.avgRating < 4);
    }

    // If no cards matched, return all
    return filtered.length > 0 ? filtered : cards;
  }, [cards, familiarity]);

  // Precompute initial flippedMap before first render
  const initialFlipMap = useMemo(() => {
    const map: Record<string, boolean> = {};
    filteredCards.forEach((card) => {
      if (cardDisplay === "Front") map[card.id] = false;
      else if (cardDisplay === "Back") map[card.id] = true;
      else if (cardDisplay === "Any") {
        map[card.id] = Math.random() < 0.5;
      }
    });
    return map;
  }, [filteredCards, cardDisplay]);

  const [flippedMap, setFlippedMap] =
    useState<Record<string, boolean>>(initialFlipMap);

  // Reset flippedMap whenever card set or display mode changes
  useEffect(() => {
    setFlippedMap(initialFlipMap);
  }, [initialFlipMap]);

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

  const activeIndex = useMemo(() => {
    const currentCard = cards[currentIndex];
    if (!currentCard) return 0;

    const i = filteredCards.findIndex((c) => c.id === currentCard.id);
    return i >= 0 ? i : 0;
  }, [cards, currentIndex, filteredCards]);

  const translateX =
    -(activeIndex * fullCardWidth) + (windowWidth - cardWidth) / 2;

  const moveToFilteredIndex = (filteredIdx: number) => {
    if (filteredIdx < 0 || filteredIdx >= filteredCards.length) return;
    const nextCard = filteredCards[filteredIdx];
    const newIndex = cards.findIndex((c) => c.id === nextCard.id);
    setCurrentIndex?.(newIndex);
  };

  useEffect(() => {
    if (filteredCards.length > 0) {
      const firstCard = filteredCards[0];
      const newIndex = cards.findIndex((c) => c.id === firstCard.id);
      setCurrentIndex?.(newIndex);
    }
  }, [filteredCards, cards, setCurrentIndex]);

  const handleMoveRight = () => {
    moveToFilteredIndex(activeIndex + 1);
  };

  const handleMoveLeft = () => {
    moveToFilteredIndex(activeIndex - 1);
  };

  return (
    <div className="relative w-full py-6">
      <div
        className="flex transition-transform duration-500"
        style={{ transform: `translateX(${translateX}px)` }}
      >
        {filteredCards.map((card, i) => {
          const distance = Math.abs(i - activeIndex);
          let scale = i === activeIndex ? 1 : 0.9;
          if (i !== activeIndex && windowWidth < 640) scale = 0.8;
          const translateY = i === activeIndex ? 0 : distance * 8;
          const flipped = flippedMap[card.id] ?? false;

          return (
            <div
              key={card.id}
              className="flex-shrink-0 relative"
              style={{
                width: cardWidth,
                margin: `0 ${cardMargin / 2}px`,
                transform: `scale(${scale}) translateY(${translateY}px)`,
                transition: "transform 0.5s ease",
              }}
            >
              {i === activeIndex && activeIndex > 0 && (
                <button
                  onClick={handleMoveLeft}
                  className="absolute left-0 top-1/2 -translate-x-full -translate-y-1/2 cursor-pointer
               opacity-0 animate-fadeIn"
                >
                  <CaretLeft size={23} />
                </button>
              )}

              <FlipCard
                card={card}
                flipped={flipped}
                onFlip={
                  i === activeIndex ? () => handleFlip(card.id) : undefined
                }
                showDecks={showDeckNames === "Show"}
                cardBgColor={cardColors[card.id] || "bg-white"}
                showHint={displayCurrentHint && i === activeIndex}
                isActive={i === activeIndex}
                customSizeClassName="w-80 h-50"
              />

              {i === activeIndex && activeIndex < filteredCards.length - 1 && (
                <button
                  onClick={handleMoveRight}
                  className="absolute right-0 top-1/2 translate-x-full -translate-y-1/2 cursor-pointer
               opacity-0 animate-fadeIn"
                >
                  <CaretRight size={23} />
                </button>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default CardCarousel;
