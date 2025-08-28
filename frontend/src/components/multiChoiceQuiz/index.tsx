import { useState, useMemo, useEffect, useCallback } from "react";
import FlipCard from "../flipCard";
import type { Card } from "../../types/card";
import ReviseButtons from "../reviseButtons";

interface MultipleChoiceQuizProps {
  cards: Card[];
  onEditCard?: (card: Card) => void;
}

const NUM_OPTIONS = 4;

const shuffleCards = (cards: Card[]) => {
  const copy = [...cards];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
};

const MultipleChoiceQuiz = ({ cards, onEditCard }: MultipleChoiceQuizProps) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [optionsState, setOptionsState] = useState<number[]>([]); // remaining option IDs
  const [selectedCorrect, setSelectedCorrect] = useState(false);
  const [showHint, setShowHint] = useState(false);

  // Compute current question and options
  const { questionCard, options } = useMemo(() => {
    if (cards.length === 0) return { questionCard: null, options: [] };
    const questionCard = cards[currentIndex % cards.length];
    const distractors = shuffleCards(
      cards.filter((c) => c.id !== questionCard.id)
    ).slice(0, NUM_OPTIONS - 1);
    const optionCards = shuffleCards([questionCard, ...distractors]);
    return { questionCard, options: optionCards };
  }, [cards, currentIndex]);

  // Reset optionsState when options change
  useEffect(() => {
    if (options.length > 0) {
      setOptionsState(options.map((c) => c.id));
      setSelectedCorrect(false);
      setShowHint(false);
    }
  }, [options]);

  // Handlers
  const handleSelect = useCallback(
    (cardId: number) => {
      if (!questionCard) return;

      if (cardId === questionCard.id) {
        setSelectedCorrect(true);
        setTimeout(() => setCurrentIndex((prev) => prev + 1), 1000);
      } else {
        // Grey out wrong option
        setOptionsState((prev) => prev.filter((id) => id !== cardId));
      }
    },
    [questionCard]
  );

  const handleEditCard = useCallback(() => {
    if (onEditCard && questionCard) onEditCard(questionCard);
  }, [onEditCard, questionCard]);

  const toggleHint = useCallback(() => {
    if (questionCard?.hintFront || questionCard?.hintBack) {
      setShowHint((prev) => !prev);
    }
  }, [questionCard]);

  if (!questionCard) return <p>No cards available!</p>;

  return (
    <div className="flex flex-col items-center py-6 space-y-6 w-full">
      {/* Revise Buttons */}
      <ReviseButtons
        showHintButton={!!(questionCard.hintFront || questionCard.hintBack)}
        onEdit={handleEditCard}
        onShowHint={toggleHint}
      />

      {/* Question */}
      <div className="mb-4 cursor-default">
        <FlipCard
          card={questionCard}
          flipped={selectedCorrect}
          showDecks={true}
          cardBgColor="bg-white"
          showHint={showHint}
          isActive={false}
        />
      </div>

      {/* Options */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 w-full max-w-screen-sm">
        {options.map((optionCard) => {
          const isDisabled = !optionsState.includes(optionCard.id);
          const isCorrect =
            selectedCorrect && optionCard.id === questionCard.id;

          return (
            <button
              key={optionCard.id}
              onClick={() => handleSelect(optionCard.id)}
              disabled={isDisabled}
              className={`border-2 border-black rounded-lg p-2 text-center transition cursor-pointer
                ${isCorrect ? "bg-green-200 hover:bg-green-300" : ""}
                ${
                  isDisabled && !isCorrect
                    ? "bg-gray-200"
                    : "bg-yellow-200 hover:bg-yellow-300"
                }`}
            >
              {optionCard.back}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default MultipleChoiceQuiz;
