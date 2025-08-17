import type { Card } from "../../../types/card";
import CardListItem from "../cardListItem";

interface CardListProps {
  cards: Card[];
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
