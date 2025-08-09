import { Cards } from "phosphor-react";
import DeckListItem from "../deckListItem";
import AddDeckButton from "../addDeckButton";
import { useNavigate } from "react-router-dom";

export interface Deck {
  id: string;
  name: string;
  totalCards?: number;
}

export interface DeckListProps {
  decks: Deck[];
  onAddDeck: (name: string) => void;
}

const DeckList = ({ decks, onAddDeck }: DeckListProps) => {
  const navigate = useNavigate();

  const handleDeckClick = (id: string) => {
    navigate(`/decks/${id}`);
  };

  return (
    <div className="flex flex-col items-center gap-2 m-2 max-w-xs mx-auto w-full">
      <DeckListItem
        id="all"
        deckName={"all cards"}
        className={"bg-pink-200"}
        Icon={Cards}
        onClick={() => handleDeckClick("all")}
      />
      {decks.map((deck) => (
        <DeckListItem
          key={deck.id}
          id={deck.id}
          deckName={deck.name}
          onClick={() => handleDeckClick(deck.id)}
          totalCards={deck.totalCards}
        />
      ))}
      <AddDeckButton onAddDeck={onAddDeck} />
    </div>
  );
};

export default DeckList;
