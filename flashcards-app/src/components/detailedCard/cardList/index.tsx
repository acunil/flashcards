import type { CardResponse } from "../../../types/cardResponse";
import CardListItem from "../cardListItem";

interface CardListProps {
  cards: CardResponse[];
}

const CardList = ({ cards }: CardListProps) => {
  return (
    <div className="flex flex-col gap-4 max-w-xl w-full mx-auto">
      {cards.map((card) => (
        <CardListItem key={card.id} {...card} />
      ))}
    </div>
  );
};

export default CardList;
