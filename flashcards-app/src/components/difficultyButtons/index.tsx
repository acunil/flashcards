import { Smiley, Skull } from "phosphor-react";

const levels = [
  { label: "Easy", Icon: Smiley },
  { label: "Medium", Icon: Smiley },
  { label: "Hard", Icon: Smiley },
  { label: "Very Hard", Icon: Smiley },
  { label: "Impossible", Icon: Skull },
];

const DifficultyButtons = () => {
  return (
    <div className="flex justify-between gap-2 mt-4 w-full max-w-md">
      {levels.map(({ label, Icon }) => (
        <button
          key={label}
          className="flex flex-col items-center p-2 border rounded hover:bg-gray-100 w-full"
        >
          <Icon size={24} />
          <span className="text-xs mt-1">{label}</span>
        </button>
      ))}
    </div>
  );
};

export default DifficultyButtons;
