import { LightbulbFilament, Pencil } from "phosphor-react";

interface ReviseButtonProps {
  showHintButton: boolean;
  onEdit: () => void;
  onShowHint: () => void;
}

const ReviseButtons = ({
  showHintButton,
  onEdit,
  onShowHint,
}: ReviseButtonProps) => {
  return (
    <div className="flex flex-row justify-end py-2 px-4 w-sm gap-2 max-w-screen">
      {showHintButton && (
        <button
          onClick={onShowHint}
          className={`p-2 border-black border-2 rounded  bg-yellow-200 hover:bg-yellow-300 cursor-pointer`}
        >
          <LightbulbFilament size={20} />
        </button>
      )}
      <button
        onClick={onEdit}
        className="bg-blue-200 p-2 border-black border-2 rounded cursor-pointer hover:bg-sky-300"
      >
        <Pencil size={20} />
      </button>
    </div>
  );
};
export default ReviseButtons;
