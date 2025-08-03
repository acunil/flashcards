import { Smiley, SmileyXEyes, SmileyMeh, Lightning } from "phosphor-react";

const levels = [
  { label: "Impossible", Icon: SmileyXEyes, buttonClassName: "bg-red-300" },
  { label: "Very Hard", Icon: Smiley, buttonClassName: "bg-orange-300" },
  { label: "Hard", Icon: SmileyMeh, buttonClassName: "bg-yellow-200" },
  { label: "Medium", Icon: Smiley, buttonClassName: "bg-green-200" },
  { label: "Easy", Icon: Lightning, buttonClassName: "bg-sky-200" },
];

const DifficultyButtons = () => {
  return (
    <div className="flex justify-between gap-2 mt-4 w-full max-w-md">
      {levels.map(({ label, Icon, buttonClassName }) => (
        <button
          key={label}
          className={`flex flex-col items-center p-2 border rounded hover:bg-gray-100 w-full cursor-pointer ${buttonClassName}`}
        >
          <Icon size={24} />
          <span className="text-xs mt-1">{label}</span>
        </button>
      ))}
    </div>
  );
};

export default DifficultyButtons;
