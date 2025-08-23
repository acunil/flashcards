import { useNavigate } from "react-router-dom";
import {
  GraduationCap,
  Barbell,
  Cards,
  FilePlus,
  ChartBar,
  UploadSimple,
} from "phosphor-react";

const buttons = [
  {
    label: "revise",
    path: "/revise",
    buttonClassName: "bg-pink-200 hover:bg-pink-400",
    Icon: GraduationCap,
  },
  {
    label: "revise hard",
    path: "/revise?hardMode=true",
    buttonClassName: "bg-pink-300 hover:bg-pink-400",
    Icon: Barbell,
  },
  {
    label: "decks",
    path: "/decks",
    buttonClassName: "bg-sky-200 hover:bg-sky-300",
    Icon: Cards,
  },
  {
    label: "add card",
    path: "/add-card",
    buttonClassName: "bg-yellow-200 hover:bg-yellow-300",
    Icon: FilePlus,
  },
  {
    label: "bulk upload",
    path: "/upload",
    buttonClassName: "bg-green-200 hover:bg-green-300",
    Icon: UploadSimple,
  },
  {
    label: "user stats",
    path: "/user-stats",
    buttonClassName: "bg-gray-200 hover:bg-gray-300",
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
          className={`relative flex items-center text-black py-3 px-4 w-50 rounded shadow-lg cursor-pointer border-black border-2 ${buttonClassName}`}
        >
          <span className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 font-medium">
            {label}
          </span>
          <Icon size={20} className="ml-auto" weight="regular" />
        </button>
      ))}
    </div>
  );
};

export default HomeButtons;
