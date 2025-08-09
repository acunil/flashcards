import { GraduationCap, type IconProps } from "phosphor-react";
import type { ComponentType, MouseEventHandler } from "react";

interface DeckListItemProps {
  id: string;
  deckName: string;
  className?: string;
  Icon?: ComponentType<IconProps>;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}

const DeckListItem = ({
  id,
  deckName,
  className = "",
  Icon,
  onClick,
}: DeckListItemProps) => {
  return (
    <div className="flex flex-row gap-1 w-full">
      <button
        onClick={onClick}
        className={`relative flex items-center text-black py-3 px-4 w-[80%] rounded shadow-lg cursor-pointer hover:bg-gray-200 border-black border-2 ${className}`}
        aria-label={`Select deck ${deckName}`}
        data-id={id}
        type="button"
      >
        {Icon && <Icon size={20} className="mr-2" weight="regular" />}
        {deckName}
      </button>
      <button
        onClick={() => {}}
        className="relative flex items-center text-black py-3 px-4 rounded shadow-lg cursor-pointer hover:bg-yellow-200 border-black border-2"
      >
        <GraduationCap size={20} />
      </button>
    </div>
  );
};

export default DeckListItem;
