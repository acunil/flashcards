import { useState, useEffect, useMemo } from "react";
import FlipCard from "../flipCard";
import type { Card } from "../../types/card";
import { useReviseSettings } from "../../hooks/reviseSettings";
import { CaretLeft, CaretRight } from "phosphor-react";

interface CardCarouselProps {
  cards: Card[];
  currentIndex: number;
  setCurrentIndex?: (index: number) => void;
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

  // Filter cards based on familiarity
  const filteredCards = useMemo(() => {
    if (familiarity === "All") return cards;

    const filtered =
      familiarity === "Hard"
        ? cards.filter((c) => c.avgRating >= 4)
        : cards.filter((c) => c.avgRating < 4);

    return filtered.length ? filtered : cards;
  }, [cards, familiarity]);

  // Initial flipped map
  const initialFlipMap = useMemo(() => {
    const map: Record<string, boolean> = {};
    filteredCards.forEach((card) => {
      if (cardDisplay === "Front") map[card.id] = false;
      else if (cardDisplay === "Back") map[card.id] = true;
      else map[card.id] = Math.random() < 0.5;
    });
    return map;
  }, [filteredCards, cardDisplay]);

  const [flippedMap, setFlippedMap] =
    useState<Record<string, boolean>>(initialFlipMap);

  useEffect(() => {
    setFlippedMap(initialFlipMap);
  }, [initialFlipMap]);

  // Window resize
  useEffect(() => {
    const handleResize = () => setWindowWidth(window.innerWidth);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const cardMargin = 8;
  const activeCardScale = 1;
  const sideScale = windowWidth < 640 ? 0.75 : 0.9;
  const cardWidthPx = windowWidth < 390 ? windowWidth * 0.8 : 320;

  const handleFlip = (cardId: number) => {
    setFlippedMap((prev) => ({ ...prev, [cardId]: !prev[cardId] }));
  };

  const activeIndex = useMemo(() => {
    const currentCard = cards[currentIndex];
    if (!currentCard) return 0;
    const i = filteredCards.findIndex((c) => c.id === currentCard.id);
    return i >= 0 ? i : 0;
  }, [cards, currentIndex, filteredCards]);

  const translateX =
    -activeIndex * (cardWidthPx + cardMargin) +
    windowWidth / 2 -
    cardWidthPx / 2;

  const moveToFilteredIndex = (idx: number) => {
    if (idx < 0 || idx >= filteredCards.length) return;
    const nextCard = filteredCards[idx];
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

  const handleMoveLeft = () => moveToFilteredIndex(activeIndex - 1);
  const handleMoveRight = () => moveToFilteredIndex(activeIndex + 1);

  const sizeClass = windowWidth < 390 ? "w-[80vw] h-[50vw]" : "w-80 h-50";

  return (
    <div className="relative w-full py-6">
      <div
        className="flex transition-transform duration-500"
        style={{ transform: `translateX(${translateX}px)` }}
      >
        {filteredCards.map((card, i) => {
          const distance = Math.abs(i - activeIndex);
          const scale = i === activeIndex ? activeCardScale : sideScale;
          const translateY = i === activeIndex ? 0 : distance * 8;
          const flipped = flippedMap[card.id] ?? false;

          return (
            <div
              key={card.id}
              className="flex-shrink-0 relative"
              style={{
                width: cardWidthPx,
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
                customSizeClassName={sizeClass}
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
