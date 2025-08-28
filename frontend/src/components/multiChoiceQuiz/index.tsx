import { useState, useMemo, useEffect } from "react";
import FlipCard from "../flipCard";
import type { Card } from "../../types/card";

interface MultipleChoiceQuizProps {
  cards: Card[];
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

const MultipleChoiceQuiz = ({ cards }: MultipleChoiceQuizProps) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [optionsState, setOptionsState] = useState<number[]>([]); // IDs of remaining options
  const [incorrectOptions, setIncorrectOptions] = useState<Set<number>>(
    new Set()
  );
  const [selectedCorrect, setSelectedCorrect] = useState(false);

  // Compute current question and options
  const { questionCard, options } = useMemo(() => {
    if (cards.length === 0) return { questionCard: null, options: [] };

    const questionCard = cards[currentIndex % cards.length];

    // Pick NUM_OPTIONS - 1 distractors
    const distractors = shuffleCards(
      cards.filter((c) => c.id !== questionCard.id)
    ).slice(0, NUM_OPTIONS - 1);

    const optionCards = shuffleCards([questionCard, ...distractors]);
    return { questionCard, options: optionCards };
  }, [cards, currentIndex]);

  // Reset options state whenever question changes
  useEffect(() => {
    if (options.length > 0) {
      setOptionsState(options.map((c) => c.id));
      setIncorrectOptions(new Set());
      setSelectedCorrect(false);
    }
  }, [options]);

  if (!questionCard) return <p>No cards available!</p>;

  const handleSelect = (cardId: number) => {
    if (cardId === questionCard.id) {
      // Correct answer
      setSelectedCorrect(true);

      // Move to next question after a short delay to show flip
      setTimeout(() => setCurrentIndex((prev) => prev + 1), 1000);
    } else {
      // Wrong answer → grey out
      setIncorrectOptions((prev) => new Set(prev).add(cardId));
    }
  };

  return (
    <div className="flex flex-col items-center py-6 space-y-6">
      {/* Question */}
      <div className="mb-4">
        <FlipCard
          card={questionCard}
          flipped={selectedCorrect}
          showDecks={true}
          cardBgColor="bg-white"
          showHint={false}
          isActive={false}
        />
      </div>

      {/* Options */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        {optionsState.map((optionId) => {
          const optionCard = options.find((c) => c.id === optionId)!;
          const isIncorrect = incorrectOptions.has(optionCard?.id);

          return (
            <button
              key={optionCard?.id}
              onClick={() => handleSelect(optionCard?.id)}
              className={`border-2 border-black rounded-lg p-2 text-center transition
                ${
                  selectedCorrect && optionCard?.id === questionCard.id
                    ? "bg-green-200 hover:bg-green-300"
                    : ""
                }
                ${
                  isIncorrect
                    ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                    : "bg-yellow-200 hover:bg-yellow-300 cursor-pointer"
                }
              `}
              disabled={isIncorrect || selectedCorrect}
            >
              {optionCard?.back}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default MultipleChoiceQuiz;
