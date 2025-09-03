import { getClosestLevel } from "../../difficultyButtons/levels";
import { useState, useRef } from "react";
import { Pencil, Check, LightbulbFilament } from "phosphor-react";
import type { Card } from "../../../types/card";
import { useNavigate } from "react-router-dom";
import { useAppContext } from "../../../contexts";
interface CardListItemProps extends Card {
  onUpdate?: (updated: { id: number; front: string; back: string }) => void;
  onDelete?: (id: number) => void;
  isSelectMode?: boolean; // new prop
  selected?: boolean; // new prop
  onToggleSelect?: (id: number) => void; // new prop
  onEditingChange?: (editing: boolean) => void;
  isAllCardsList?: boolean;
}

const CardListItem = ({
  id,
  front,
  back,
  decks,
  hintFront,
  hintBack,
  viewCount,
  avgRating,
  lastRating,
  onUpdate,
  isSelectMode = false,
  selected = false,
  onToggleSelect,
  onEditingChange,
  isAllCardsList = true,
}: CardListItemProps) => {
  const lastLevel = getClosestLevel(lastRating);
  const avgLevel = getClosestLevel(avgRating);
  const { selectedSubject } = useAppContext();

  const [frontValue, setFrontValue] = useState(front);
  const [backValue, setBackValue] = useState(back);
  const [frontHint, setFrontHint] = useState(hintFront);
  const [backHint, setBackHint] = useState(hintBack);
  const [isEditingLocal, setIsEditingLocal] = useState(false);

  const frontRef = useRef<HTMLTextAreaElement>(null);
  const backRef = useRef<HTMLTextAreaElement>(null);
  const navigate = useNavigate();

  const autoResize = (textarea: HTMLTextAreaElement | null) => {
    if (!textarea) return;
    textarea.style.height = "auto";
    textarea.style.height = textarea.scrollHeight + "px";
  };

  const handleSave = () => {
    onUpdate?.({ id, front: frontValue, back: backValue });
    setIsEditingLocal(false);
    onEditingChange?.(false);
  };

  const handleCardClick = () => {
    if (isSelectMode) {
      onToggleSelect?.(id);
    }
  };

  const handleEditClick = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    navigate(`/add-card/${id}`);
    return;

    // used to activate inline editing - this feature is no longer desired but leaving here in case we go back to it
    if (!isSelectMode) {
      if (isEditingLocal) {
        handleSave();
      } else {
        setIsEditingLocal(true);
        onEditingChange?.(true);
      }
    }
  };

  return (
    <div
      onClick={handleCardClick}
      className={`flex border-2 rounded-lg overflow-hidden w-full mx-auto shadow-lg transition-shadow duration-100 
                
                ${isSelectMode ? "cursor-pointer" : ""}
                ${isSelectMode && selected ? "bg-red-50 border-red-400" : ""}`}
    >
      {/* Middle content */}
      <div className="flex flex-col flex-1">
        {/* Top row: front + back */}
        <div className="flex flex-row w-full">
          {/* Front */}
          <div className="p-4 flex-1">
            {isEditingLocal ? (
              <div className="flex flex-col gap-2">
                <label className="text-xs font-medium text-gray-500">
                  {selectedSubject?.frontLabel || "Front"}
                </label>
                <textarea
                  ref={frontRef}
                  value={frontValue}
                  onChange={(e) => {
                    setFrontValue(e.target.value);
                    autoResize(e.target);
                  }}
                  className="w-full resize-none border border-gray-300 rounded p-1 outline-none text-center font-semibold"
                  rows={2}
                />
                <label className="text-xs font-medium text-gray-500">
                  {`${selectedSubject?.frontLabel ?? "Front"} hint`}
                </label>
                <textarea
                  value={frontHint}
                  onChange={(e) => {
                    setFrontHint(e.target.value);
                    autoResize(e.target);
                  }}
                  className="w-full resize-none border border-gray-300 rounded p-1 outline-none text-center text-gray-600"
                  rows={1}
                />
              </div>
            ) : (
              <div className="flex flex-col gap-1 text-center">
                <p className="font-semibold">{frontValue}</p>
                {hintFront && (
                  <div className="flex items-center justify-center gap-1 text-gray-600 font-light px-2 py-1 border border-gray-200 rounded-lg bg-gray-50">
                    <LightbulbFilament size={18} />
                    {frontHint}
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Back */}
          <div className="p-4 flex-1">
            {isEditingLocal ? (
              <div className="flex flex-col gap-2">
                <label className="text-xs font-medium text-gray-500">
                  {selectedSubject?.backLabel || "Back"}
                </label>
                <textarea
                  ref={backRef}
                  value={backValue}
                  onChange={(e) => {
                    setBackValue(e.target.value);
                    autoResize(e.target);
                  }}
                  className="w-full resize-none border border-gray-300 rounded p-1 outline-none text-center font-semibold"
                  rows={2}
                />
                <label className="text-xs font-medium text-gray-500">
                  {`${selectedSubject?.backLabel ?? "Back"} hint`}
                </label>
                <textarea
                  value={backHint}
                  onChange={(e) => {
                    setBackHint(e.target.value);
                    autoResize(e.target);
                  }}
                  className="w-full resize-none border border-gray-300 rounded p-1 outline-none text-center text-gray-600"
                  rows={1}
                />
              </div>
            ) : (
              <div className="flex flex-col gap-1 text-center">
                <p className="font-semibold">{backValue}</p>
                {hintBack && (
                  <div className="flex items-center justify-center gap-1 text-gray-600 font-light px-2 py-1 border border-gray-200 rounded-lg bg-gray-50">
                    <LightbulbFilament size={18} />
                    {backHint}
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Decks */}
        {isAllCardsList && decks.length > 0 && (
          <div className="flex flex-row flex-wrap gap-2 mb-4 text-sm justify-center">
            {decks.map((deck) => (
              <div
                key={deck.id}
                className="flex gap-1 text-sm bg-sky-200 font-light text-black px-3 py-1 rounded-full text-md border-none border-black"
              >
                {deck.name}
              </div>
            ))}
          </div>
        )}

        {/* Bottom row: stats */}
        <div className="flex flex-row justify-around items-center p-3 text-sm bg-gray-50 border-t border-gray-200">
          <div className="flex items-center space-x-2">
            <span>views:</span>
            <span>{viewCount || 0}</span>
          </div>
          <div className="flex items-center space-x-2">
            <span>last:</span>
            {lastRating != null ? (
              <lastLevel.Icon
                size={20}
                weight="duotone"
                color={lastLevel.color}
              />
            ) : (
              <span>-</span>
            )}
          </div>
          <div className="flex items-center space-x-2">
            <span>avg:</span>
            {avgRating != null ? (
              <avgLevel.Icon
                size={20}
                weight="duotone"
                color={avgLevel.color}
              />
            ) : (
              <span>-</span>
            )}
          </div>
        </div>
      </div>

      {/* Right side: full-panel edit/save */}
      {onUpdate && (
        <div
          onClick={(e) => {
            handleEditClick(e);
          }}
          className={`flex flex-col justify-center items-center p-2 min-w-1/10 cursor-pointer 
                    ${
                      isSelectMode
                        ? "bg-gray-300"
                        : "bg-yellow-100 hover:bg-yellow-200"
                    }`}
        >
          {isEditingLocal ? (
            <Check size={20} className="text-green-700" />
          ) : (
            <Pencil size={20} className="text-yellow-700" />
          )}
        </div>
      )}
    </div>
  );
};

export default CardListItem;
