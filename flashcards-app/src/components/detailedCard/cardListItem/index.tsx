import { NotePencil } from "phosphor-react";
import { getClosestLevel } from "../../difficultyButtons/levels";
import { useNavigate } from "react-router-dom";
import type { CardResponse } from "../../../types/cardResponse";

const CardListItem = ({
  id,
  front,
  back,
  viewCount,
  avgRating,
  lastRating,
}: CardResponse) => {
  const lastLevel = getClosestLevel(lastRating);
  const avgLevel = getClosestLevel(avgRating);

  const navigate = useNavigate();

  const handleEditClick = () => {
    // TODO
    navigate("/add-card"); // or your edit route
  };

  return (
    <div
      className="flex border-2 rounded-lg overflow-hidden w-full mx-auto shadow-lg"
      aria-label={`Card ${id}`}
    >
      {/* Left side */}
      <div className="flex flex-col w-2/3 border-r border-gray-200 relative">
        <div className="p-4 border-b border-gray-200 flex-grow flex items-center justify-center">
          <p className="font-semibold truncate">{front}</p>
        </div>
        <div className="p-4 flex-grow flex items-center justify-center">
          <p className="text-gray-700 truncate">{back}</p>
        </div>

        {/* Edit icon at the bottom */}
        <button
          aria-label={`Edit card ${id}`}
          className="absolute bottom-2 right-2 text-gray-600 cursor-pointer hover:text-gray-900"
          onClick={handleEditClick}
        >
          <NotePencil size={22} weight="regular" />
        </button>
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
