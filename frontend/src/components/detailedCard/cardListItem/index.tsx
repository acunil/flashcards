import { getClosestLevel } from "../../difficultyButtons/levels";
import { useState, useEffect, useRef } from "react";
import { Trash, Pencil, Check, LightbulbFilament } from "phosphor-react";
import type { Card } from "../../../types/card";

interface CardListItemProps extends Card {
  onUpdate?: (updated: { id: number; front: string; back: string }) => void;
  onDelete?: (id: number) => void;
}

const CardListItem = ({
  id,
  front,
  back,
  viewCount,
  avgRating,
  lastRating,
  onUpdate,
  onDelete,
}: CardListItemProps) => {
  const lastLevel = getClosestLevel(lastRating);
  const avgLevel = getClosestLevel(avgRating);

  const [frontValue, setFrontValue] = useState(front);
  const [backValue, setBackValue] = useState(back);
  const [frontHint, setFrontHint] = useState("front hint");
  const [backHint, setBackHint] = useState("back hint");
  const [isEditingLocal, setIsEditingLocal] = useState(false);

  const frontRef = useRef<HTMLTextAreaElement>(null);
  const backRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (isEditingLocal) {
      frontRef.current?.focus();
      if (frontRef.current) {
        frontRef.current.selectionStart = frontRef.current.selectionEnd =
          frontRef.current.value.length;
        autoResize(frontRef.current);
      }
      autoResize(backRef.current);
    }
  }, [isEditingLocal]);

  const autoResize = (textarea: HTMLTextAreaElement | null) => {
    if (!textarea) return;
    textarea.style.height = "auto";
    textarea.style.height = textarea.scrollHeight + "px";
  };

  const handleSave = () => {
    onUpdate?.({ id, front: frontValue, back: backValue });
    setIsEditingLocal(false);
  };

  return (
    <div className="flex border-2 rounded-lg overflow-hidden w-full mx-auto shadow-lg transition-shadow duration-100 hover:shadow-2xl">
      {/* Middle content */}
      <div className="flex flex-col flex-1">
        {/* Top row: front + back */}
        <div className="flex flex-row w-full border-b border-gray-200">
          {/* Front */}
          <div className="p-4 flex-1">
            {isEditingLocal ? (
              <div className="flex flex-col gap-2">
                {/* Front Text */}
                <label className="text-xs font-medium text-gray-500">
                  Front
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

                {/* Front Hint */}
                <label className="text-xs font-medium text-gray-500">
                  Front Hint
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
                <p className="font-semibold mb-2">{frontValue}</p>
                <div className="flex items-center justify-center gap-1 text-gray-600 font-light px-2 py-1 border border-gray-200 rounded-lg bg-gray-50">
                  <LightbulbFilament size={18} />
                  {frontHint}
                </div>
              </div>
            )}
          </div>

          {/* Back */}
          <div className="p-4 flex-1">
            {isEditingLocal ? (
              <div className="flex flex-col gap-2">
                {/* Back Text */}
                <label className="text-xs font-medium text-gray-500">
                  Back
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

                {/* Back Hint */}
                <label className="text-xs font-medium text-gray-500">
                  Hint
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
                <p className="font-semibold mb-2">{backValue}</p>
                <div className="flex items-center justify-center gap-1 text-gray-600 font-light px-2 py-1 border border-gray-200 rounded-lg bg-gray-50">
                  <LightbulbFilament size={18} />
                  {backHint}
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Bottom row: stats */}
        <div className="flex flex-row justify-around items-center p-3 text-sm bg-gray-50">
          <div className="flex items-center space-x-2">
            <span>views:</span>
            <span>{viewCount}</span>
          </div>
          <div className="flex items-center space-x-2">
            <span>last:</span>
            <lastLevel.Icon
              size={20}
              weight="duotone"
              color={lastLevel.color}
            />
          </div>
          <div className="flex items-center space-x-2">
            <span>avg:</span>
            <avgLevel.Icon size={20} weight="duotone" color={avgLevel.color} />
          </div>
        </div>
      </div>

      {/* Right side: edit/save & delete */}
      <div className="flex flex-col justify-center items-center space-y-2 p-2 bg-yellow-100 ">
        {isEditingLocal && onDelete && (
          <button
            onClick={() => onDelete(id)}
            className="flex items-center justify-center w-10 h-10 bg-red-100 rounded hover:bg-red-200"
          >
            <Trash size={20} className="text-red-600" />
          </button>
        )}
        <button
          onClick={() => {
            if (isEditingLocal) handleSave();
            else setIsEditingLocal(true);
          }}
          className="flex items-center justify-center w-10 h-10 bg-yellow-100 rounded hover:bg-yellow-200"
        >
          {isEditingLocal ? (
            <Check size={20} className="text-green-700" />
          ) : (
            <Pencil size={20} className="text-yellow-700" />
          )}
        </button>
      </div>
    </div>
  );
};

export default CardListItem;
