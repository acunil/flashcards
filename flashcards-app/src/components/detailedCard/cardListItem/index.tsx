import { getClosestLevel } from "../../difficultyButtons/levels";

export interface CardListItemProps {
  id: string;
  textFront: string;
  textBack: string;
  viewCount: number;
  avgRating: number;
  lastRating: number;
}

const CardListItem = ({
  id,
  textFront,
  textBack,
  viewCount,
  avgRating,
  lastRating,
}: CardListItemProps) => {
  const lastLevel = getClosestLevel(lastRating);
  const avgLevel = getClosestLevel(avgRating);
  return (
    <div
      className="flex border-2 rounded-lg overflow-hidden w-full mx-auto"
      aria-label={`Card ${id}`}
    >
      {/* Left side */}
      <div className="flex flex-col w-2/3 border-r border-gray-200">
        <div className="p-4 border-b border-gray-200 flex-grow flex items-center justify-center">
          <p className="font-semibold truncate">{textFront}</p>
        </div>
        <div className="p-4 flex-grow flex items-center justify-center">
          <p className="text-gray-700 truncate">{textBack}</p>
        </div>
      </div>

      {/* Right side */}
      <div className="w-1/3 border-l border-gray-200 flex flex-col justify-center p-4 space-y-4 text-sm">
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
