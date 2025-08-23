import { Cards } from "phosphor-react";
import DeckListItem from "../deckListItem";
import AddDeckButton from "../addDeckButton";
import { useNavigate } from "react-router-dom";
import type { Deck } from "../../../types/deck";
import { useAppContext } from "../../../contexts";

export interface DeckListProps {
  decks: Deck[];
  onAddDeck: (name: string) => void;
}

const DeckList = ({ decks, onAddDeck }: DeckListProps) => {
  const navigate = useNavigate();
  const { cards, selectedSubjectId } = useAppContext();

  const handleDeckClick = (id: number) => {
    navigate(`/decks/${id}`);
  };

  const totalCardCount = cards.length;

  const getDeckCardCount = (deckId: number) => {
    return cards.filter((card) => card.decks.some((deck) => deck.id === deckId))
      .length;
  };

  return (
    <div className="flex flex-col items-center gap-2 m-2 p-2 mb-4 max-w-xs mx-auto w-full">
      <DeckListItem
        deck={{ id: 0, name: "all cards", subjectId: selectedSubjectId || 0 }}
        className={"bg-pink-200"}
        Icon={Cards}
        onClick={() => handleDeckClick(0)}
        totalCards={totalCardCount}
      />
      {decks.map((deck) => (
        <DeckListItem
          key={deck.id}
          deck={deck}
          onClick={() => handleDeckClick(deck.id)}
          totalCards={getDeckCardCount(deck.id)}
        />
      ))}
      <AddDeckButton onAddDeck={onAddDeck} />
    </div>
  );
};

export default DeckList;
