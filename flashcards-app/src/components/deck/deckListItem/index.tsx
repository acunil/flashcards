import type { IconProps } from "phosphor-react";
import type { ComponentType } from "react";

interface DeckListItemProps {
  deckName: string;
  className?: string;
  Icon?: ComponentType<IconProps>;
}

const DeckListItem = ({
  deckName,
  className = "",
  Icon,
}: DeckListItemProps) => {
  return (
    <>
      <button
        key={deckName}
        onClick={() => {}}
        className={`relative flex items-center text-black py-3 px-4 w-60 rounded shadow-lg cursor-pointer hover:bg-gray-200 border-black border-2 ${
          className ?? ""
        }`}
      >
        {Icon && <Icon size={20} className="mr-2" weight="regular" />}
        {deckName}
      </button>
      <button></button>
    </>
  );
};

export default DeckListItem;
