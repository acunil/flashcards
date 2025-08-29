import { useState, useMemo, useEffect, useCallback } from "react";
import FlipCard from "../flipCard";
import type { Card } from "../../types/card";
import ReviseButtons from "../reviseButtons";
import { useNavigate } from "react-router-dom";

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
  const [optionsState, setOptionsState] = useState<number[]>([]); // remaining option IDs
  const [selectedCorrect, setSelectedCorrect] = useState(false);
  const [showHint, setShowHint] = useState(false);
  const [showAnswer, setShowAnswer] = useState(false);
  const navigate = useNavigate();

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

  const handleSelect = useCallback(
    (cardId: number) => {
      if (!questionCard) return;

      if (cardId === questionCard.id) {
        setShowAnswer(true);

        setTimeout(() => {
          setShowAnswer(false);
          setCurrentIndex((prev) => prev + 1);
        }, 1000);
      } else {
        setOptionsState((prev) => prev.filter((id) => id !== cardId));
      }
    },
    [questionCard]
  );
  const handleEditCard = () => {
    if (questionCard) {
      navigate(`/add-card/${questionCard.id}`);
    }
  };

  const toggleHint = useCallback(() => {
    if (questionCard?.hintFront || questionCard?.hintBack) {
      setShowHint((prev) => !prev);
    }
  }, [questionCard]);

  if (!questionCard) return <p>No cards available!</p>;

  return (
    <div className="flex relative flex-col items-center py-4 space-y-6 w-full">
      {/* Card + Buttons wrapper */}
      <div className="w-full max-w-md flex flex-col">
        <div className="flex justify-end mb-2">
          <ReviseButtons
            showHintButton={!!(questionCard.hintFront || questionCard.hintBack)}
            onEdit={handleEditCard}
            onShowHint={toggleHint}
          />
        </div>

        {/* FlipCard */}
        <div className="cursor-default aspect-[4/3] w-full">
          <FlipCard
            key={questionCard.id}
            card={questionCard}
            flipped={showAnswer}
            showDecks={true}
            cardBgColor="bg-white"
            showHint={showHint}
            isActive={false}
          />
        </div>
      </div>

      {/* Options */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 w-full max-w-screen-sm">
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
