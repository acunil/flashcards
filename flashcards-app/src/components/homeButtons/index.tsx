import { useNavigate } from "react-router-dom";
import {
  GraduationCap,
  Barbell,
  Cards,
  FilePlus,
  ChartBar,
} from "phosphor-react";

const buttons = [
  {
    label: "revise",
    path: "/revise",
    buttonClassName: "bg-pink-200",
    Icon: GraduationCap,
  },
  {
    label: "revise hard",
    path: "/revise-hard",
    buttonClassName: "bg-pink-300",
    Icon: Barbell,
  },
  {
    label: "decks",
    path: "/decks",
    buttonClassName: "bg-sky-200",
    Icon: Cards,
  },
  {
    label: "add card",
    path: "/add-card",
    buttonClassName: "bg-yellow-200",
    Icon: FilePlus,
  },
  {
    label: "user stats",
    path: "/user-stats",
    buttonClassName: "bg-gray-200",
    Icon: ChartBar,
  },
];

const HomeButtons = () => {
  const navigate = useNavigate();

  return (
    <div className="p-8 flex flex-col gap-4">
      {buttons.map(({ label, path, buttonClassName, Icon }) => (
        <button
          key={label}
          onClick={() => navigate(path)}
          className={`flex justify-between items-center text-black py-3 px-4 w-50 rounded shadow-lg cursor-pointer hover:bg-gray-200 border-black border-2 ${buttonClassName}`}
        >
          <span className="mx-auto font-medium">{label}</span>
          <Icon size={20} className="ml-auto" weight="regular" />
        </button>
      ))}
    </div>
  );
};

export default HomeButtons;
