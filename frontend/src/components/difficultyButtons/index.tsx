import { levels } from "./levels";

interface DifficultyButtonsProps {
  onSelectDifficulty: (rating: number) => void;
}

const DifficultyButtons = ({ onSelectDifficulty }: DifficultyButtonsProps) => {
  return (
    <div className="flex justify-between gap-2 mt-4 w-full max-w-md p-4">
      {levels.map(({ label, Icon, buttonClassName, rating }) => (
        <button
          key={label}
          className={`flex flex-col items-center p-2 border-2 rounded hover:bg-gray-100 w-full cursor-pointer ${buttonClassName} active:translate-y-1 active:shadow-inner active:bg-opacity-90 transition-transform duration-150`}
          onClick={() => onSelectDifficulty(rating)}
        >
          <Icon size={24} />
          <span className="hidden xs:block text-xs mt-1">{label}</span>
        </button>
      ))}
    </div>
  );
};

export default DifficultyButtons;
