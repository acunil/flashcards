import {
  Smiley,
  SmileyXEyes,
  SmileyMeh,
  Lightning,
  SmileyNervous,
} from "phosphor-react";

const levels = [
  { label: "Impossible", Icon: SmileyXEyes, buttonClassName: "bg-red-300" },
  { label: "Very Hard", Icon: SmileyNervous, buttonClassName: "bg-orange-300" },
  { label: "Hard", Icon: SmileyMeh, buttonClassName: "bg-yellow-200" },
  { label: "Medium", Icon: Smiley, buttonClassName: "bg-green-200" },
  { label: "Easy", Icon: Lightning, buttonClassName: "bg-sky-200" },
];

interface DifficultyButtonsProps {
  onSelectDifficulty: (levelLabel: string) => void;
}

const DifficultyButtons = ({ onSelectDifficulty }: DifficultyButtonsProps) => {
  return (
    <div className="flex justify-between gap-2 mt-4 w-full max-w-md">
      {levels.map(({ label, Icon, buttonClassName }) => (
        <button
          key={label}
          className={`flex flex-col items-center p-2 border-2 rounded hover:bg-gray-100 w-full cursor-pointer ${buttonClassName}`}
          onClick={() => onSelectDifficulty(label)}
        >
          <Icon size={24} />
          <span className="text-xs mt-1">{label}</span>
        </button>
      ))}
    </div>
  );
};

export default DifficultyButtons;
