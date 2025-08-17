import { getClosestLevel } from "../../difficultyButtons/levels";
import { useNavigate } from "react-router-dom";
import type { Card } from "../../../types/card";

interface CardListItemProps extends Card {
  clickable?: boolean;
}

const CardListItem = ({
  id,
  front,
  back,
  viewCount,
  avgRating,
  lastRating,
  clickable = true,
}: CardListItemProps) => {
  const lastLevel = getClosestLevel(lastRating);
  const avgLevel = getClosestLevel(avgRating);

  const navigate = useNavigate();

  const handleClick = () => {
    if (clickable) navigate(`/add-card/${id}`);
  };

  return (
    <div
      onClick={handleClick}
      className={`flex border-2 rounded-lg overflow-hidden w-full mx-auto shadow-lg transition-shadow duration-100 ${
        clickable
          ? "cursor-pointer hover:shadow-2xl"
          : "cursor-default hover:shadow-none"
      }`}
      aria-label={`Card ${id}`}
    >
      {/* Left side */}
      <div className="flex flex-col w-2/3 border-r border-gray-200">
        <div className="p-4 border-b border-gray-200 flex-grow flex items-center justify-center">
          <p className="font-semibold truncate">{front}</p>
        </div>
        <div className="p-4 flex-grow flex items-center justify-center">
          <p className="text-gray-700 truncate">{back}</p>
        </div>
      </div>

      {/* Right side */}
      <div className="w-1/3 border-l border-gray-400 flex flex-col justify-center p-4 space-y-4 text-sm">
        <div className="flex justify-between">
          <span>views:</span>
          <span className="mx-2">{viewCount}</span>
        </div>
        <div className="flex justify-between">
          <span>last rating:</span>
          <lastLevel.Icon size={24} weight="duotone" color={lastLevel.color} />
        </div>
        <div className="flex justify-between">
          <span>avg rating:</span>
          <avgLevel.Icon size={24} weight="duotone" color={avgLevel.color} />
        </div>
      </div>
    </div>
  );
};

export default CardListItem;
