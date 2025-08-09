import { Cards } from "phosphor-react";
import DeckListItem from "../deckListItem";

export interface DeckListProps {
  decks: string[]; // updated to use string[] instead of any
}

const DeckList = ({ decks }: DeckListProps) => {
  return (
    <div className="flex flex-col items-center gap-2 m-2">
      <DeckListItem
        deckName={"all cards"}
        className={"bg-pink-200"}
        Icon={Cards}
      />
      {decks.map((deck, index) => (
        <DeckListItem key={index} deckName={deck} />
      ))}
    </div>
  );
};

export default DeckList;
