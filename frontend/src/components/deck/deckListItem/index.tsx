import { GraduationCap, type IconProps } from "phosphor-react";
import type { ComponentType, MouseEventHandler } from "react";
import { useNavigate } from "react-router-dom";
import type { Deck } from "../../../types/deck";

interface DeckListItemProps {
  deck: Deck;
  className?: string;
  totalCards?: number;
  Icon?: ComponentType<IconProps>;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}

const DeckListItem = ({
  deck,
  className = "",
  Icon,
  onClick,
  totalCards,
}: DeckListItemProps) => {
  const navigate = useNavigate();

  const handleReviseClick = () => {
    if (totalCards && totalCards > 0) {
      navigate(`/revise/${deck.id}?hardMode=false`);
    }
  };

  return (
    <div className="flex flex-row gap-1 w-full">
      <button
        onClick={onClick}
        className={`relative flex items-center justify-between text-black py-3 px-4 w-[80%] rounded shadow-lg cursor-pointer hover:bg-gray-200 border-black border-2 ${className}`}
        aria-label={`Select deck ${deck.name}`}
        data-id={deck.id}
        type="button"
      >
        <span className="flex items-center">
          {Icon && <Icon size={20} className="mr-2" weight="regular" />}
          {deck.name}
        </span>
        {totalCards && totalCards > 0 && (
          <span className="text-gray-500">{totalCards}</span>
        )}
      </button>
      <button
        onClick={handleReviseClick}
        className="relative flex items-center text-black py-3 px-4 rounded shadow-lg cursor-pointer hover:bg-yellow-200 border-black border-2"
      >
        <GraduationCap size={20} />
      </button>
    </div>
  );
};

export default DeckListItem;
