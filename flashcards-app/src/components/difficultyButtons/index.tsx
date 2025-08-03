import {
  Smiley,
  SmileyXEyes,
  SmileyMeh,
  Lightning,
  SmileyNervous,
} from "phosphor-react";

const levels = [
  {
    label: "Impossible",
    Icon: SmileyXEyes,
    buttonClassName: "bg-red-300",
    rating: 5,
  },
  {
    label: "Very Hard",
    Icon: SmileyNervous,
    buttonClassName: "bg-orange-300",
    rating: 4,
  },
  {
    label: "Hard",
    Icon: SmileyMeh,
    buttonClassName: "bg-yellow-200",
    rating: 3,
  },
  { label: "Medium", Icon: Smiley, buttonClassName: "bg-green-200", rating: 2 },
  { label: "Easy", Icon: Lightning, buttonClassName: "bg-sky-200", rating: 1 },
];

interface DifficultyButtonsProps {
  onSelectDifficulty: (rating: number) => void;
}

const DifficultyButtons = ({ onSelectDifficulty }: DifficultyButtonsProps) => {
  return (
    <div className="flex justify-between gap-2 mt-4 w-full max-w-md">
      {levels.map(({ label, Icon, buttonClassName, rating }) => (
        <button
          key={label}
          className={`flex flex-col items-center p-2 border-2 rounded hover:bg-gray-100 w-full cursor-pointer ${buttonClassName}`}
          onClick={() => onSelectDifficulty(rating)}
        >
          <Icon size={24} />
          <span className="text-xs mt-1">{label}</span>
        </button>
      ))}
    </div>
  );
};

export default DifficultyButtons;
