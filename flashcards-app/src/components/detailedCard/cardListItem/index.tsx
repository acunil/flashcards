import { getClosestLevel } from "../../difficultyButtons/levels";
import { useState, useEffect, useRef } from "react";
import type { Card } from "../../../types/card";

interface CardListItemProps extends Card {
  isEditing?: boolean;
  onUpdate?: (updated: { id: number; front: string; back: string }) => void;
  onStartEditing?: () => void;
}

const CardListItem = ({
  id,
  front,
  back,
  viewCount,
  avgRating,
  lastRating,
  isEditing = false,
  onUpdate,
  onStartEditing,
}: CardListItemProps) => {
  const lastLevel = getClosestLevel(lastRating);
  const avgLevel = getClosestLevel(avgRating);

  const [frontValue, setFrontValue] = useState(front);
  const [backValue, setBackValue] = useState(back);

  const frontRef = useRef<HTMLTextAreaElement>(null);
  const backRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    // Only initialize when entering edit mode for the first time
    if (isEditing && frontValue === front && backValue === back) {
      setFrontValue(front);
      setBackValue(back);
    }
  }, [isEditing, front, back, frontValue, backValue]);

  useEffect(() => {
    if (isEditing) {
      const el = frontRef.current;
      if (el) {
        el.focus();
        el.selectionStart = el.selectionEnd = el.value.length; // cursor at end
        autoResize(el);
      }
      autoResize(backRef.current);
    }
  }, [isEditing]);

  const handleClick = () => {
    if (!isEditing && onStartEditing) onStartEditing();
  };

  const handleBlur = () => {
    if (onUpdate && (frontValue !== front || backValue !== back)) {
      onUpdate({ id, front: frontValue, back: backValue });
    }
  };

  // Auto-resize function
  const autoResize = (textarea: HTMLTextAreaElement | null) => {
    if (!textarea) return;
    textarea.style.height = "auto";
    textarea.style.height = textarea.scrollHeight + "px";
  };

  const handleFrontChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setFrontValue(e.target.value);
    autoResize(e.target);
  };

  const handleBackChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setBackValue(e.target.value);
    autoResize(e.target);
  };

  const seamlessStyle =
    "w-full resize-none border-0 bg-transparent p-0 text-center font-semibold outline-none text-inherit overflow-hidden";
  const seamlessBackStyle =
    "w-full resize-none border-0 bg-transparent p-0 text-center text-gray-700 outline-none text-inherit overflow-hidden";

  return (
    <div
      onClick={handleClick}
      className="flex border-2 rounded-lg overflow-hidden w-full mx-auto shadow-lg transition-shadow duration-100 cursor-pointer hover:shadow-2xl"
      aria-label={`Card ${id}`}
    >
      {/* Left side */}
      <div className="flex flex-col w-2/3 border-r border-gray-200">
        {/* Front */}
        <div className="p-4 border-b border-gray-200 flex-grow flex items-center justify-center">
          {isEditing ? (
            <textarea
              ref={frontRef}
              value={frontValue}
              onChange={handleFrontChange}
              onBlur={handleBlur}
              className={seamlessStyle}
              rows={1}
            />
          ) : (
            <p className="font-semibold truncate">{frontValue}</p> // <-- use frontValue instead of front
          )}
        </div>

        {/* Back */}
        <div className="p-4 flex-grow flex items-center justify-center">
          {isEditing ? (
            <textarea
              ref={backRef}
              value={backValue}
              onChange={handleBackChange}
              onBlur={handleBlur}
              className={seamlessBackStyle}
              rows={1}
            />
          ) : (
            <p className="text-gray-700 truncate">{backValue}</p> // <-- use backValue instead of back
          )}
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
