import { Gear, House } from "phosphor-react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Toggle from "../toggle";

interface HeaderProps {
  cardDisplay: "Front" | "Back" | "Any";
  setCardDisplay: (val: "Front" | "Back" | "Any") => void;
}

const Header = ({ cardDisplay, setCardDisplay }: HeaderProps) => {
  const navigate = useNavigate();
  const [showDropdown, setShowDropdown] = useState(false);
  const [familiarity, setFamiliarity] = useState("All");
  const [showDeckNames, setShowDeckNames] = useState("Hide");

  return (
    <header className="flex justify-between items-center px-4 py-3 border-b shadow-sm">
      <div className="p-1">
        <House
          size={24}
          className="cursor-pointer"
          onClick={() => navigate("/")}
        />
      </div>
      <div
        className={`p-1 border-2 rounded transition-colors ${
          showDropdown ? "bg-white border-black" : "border-transparent"
        }`}
      >
        <Gear
          size={24}
          className="cursor-pointer"
          onClick={() => setShowDropdown((prev) => !prev)}
        />
      </div>
      {showDropdown && (
        <div className="absolute right-4 top-9 mt-4 w-100 bg-white border-2 rounded z-10 text-sm p-3 space-y-2">
          <div className="flex items-center justify-between">
            <label>Card display:</label>
            <Toggle
              options={["Front", "Back", "Any"]}
              selected={cardDisplay}
              onChange={setCardDisplay}
            />
          </div>
          <div className="flex items-center justify-between">
            <label>Familiarity:</label>
            <Toggle
              options={["All", "Hard", "Easy"]}
              selected={familiarity}
              onChange={setFamiliarity}
            />
          </div>
          <div className="flex items-center justify-between">
            <label>Show decks:</label>
            <Toggle
              options={["Show", "Hide"]}
              selected={showDeckNames}
              onChange={setShowDeckNames}
            />
          </div>
          <div className="flex justify-end pt-2">
            <button
              onClick={() => setShowDropdown(false)}
              className="border-2 bg-sky-200 px-4 py-1 rounded-md cursor-pointer hover:bg-sky-300 transition"
            >
              Done
            </button>
          </div>
        </div>
      )}
    </header>
  );
};

export default Header;
