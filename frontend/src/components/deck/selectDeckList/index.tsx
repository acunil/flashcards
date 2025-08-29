import { Cards } from "phosphor-react";
import DeckListItem from "../deckListItem";
import type { Deck } from "../../../types/deck";
import { useAppContext } from "../../../contexts";

export interface DeckListProps {
  decks: Deck[];
  onSelectDeck: (id: number) => void;
}

const SelectDeckList = ({ decks, onSelectDeck }: DeckListProps) => {
  const { cards, selectedSubjectId } = useAppContext();
  const totalCardCount = cards.length;

  const getDeckCardCount = (deckId: number) => {
    return cards.filter((card) => card.decks.some((deck) => deck.id === deckId))
      .length;
  };

  const sortedDecks = [...decks].sort(
    (a, b) => getDeckCardCount(b.id) - getDeckCardCount(a.id)
  );

  return (
    <div className="flex flex-col gap-2 m-2 p-2 mb-4 w-full max-w-xs mx-auto">
      <DeckListItem
        deck={{ id: 0, name: "all cards", subjectId: selectedSubjectId || 0 }}
        className="bg-pink-200 w-full"
        Icon={Cards}
        onClick={() => onSelectDeck(0)}
        totalCards={totalCardCount}
      />
      {sortedDecks.map((deck) => (
        <DeckListItem
          key={deck.id}
          deck={deck}
          className="w-full"
          onClick={() => onSelectDeck(deck.id)}
          totalCards={getDeckCardCount(deck.id)}
        />
      ))}
    </div>
  );
};

export default SelectDeckList;
