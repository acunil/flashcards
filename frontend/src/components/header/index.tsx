import { Books, Cards, Gear, House } from "phosphor-react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Toggle, { type ToggleOption } from "../toggle";
import { useAppContext } from "../../contexts";
import { useReviseSettings } from "../../hooks/reviseSettings";
import type {
  CardDisplay,
  DeckVisibility,
  Familiarity,
} from "../../contexts/ReviseSettingsContext";

interface HeaderProps {
  isHomePage?: boolean;
  isRevising?: boolean;
}

const Header = ({ isHomePage = false, isRevising = false }: HeaderProps) => {
  const navigate = useNavigate();
  const [showDropdown, setShowDropdown] = useState(false);
  const { selectedSubject } = useAppContext();

  // Get context values
  const {
    cardDisplay,
    setCardDisplay,
    familiarity,
    setFamiliarity,
    showDeckNames,
    setShowDeckNames,
  } = useReviseSettings();

  const frontLabel = selectedSubject?.frontLabel || "Front";
  const backLabel = selectedSubject?.backLabel || "Back";

  const cardOptions: ToggleOption<CardDisplay>[] = [
    { display: frontLabel, value: "Front" },
    { display: backLabel, value: "Back" },
    { display: "Any", value: "Any" },
  ];

  const familiarityOptions: ToggleOption<Familiarity>[] = [
    { display: "All", value: "All" },
    { display: "Hard", value: "Hard" },
    { display: "Easy", value: "Easy" },
  ];

  const deckOptions: ToggleOption<DeckVisibility>[] = [
    { display: "Show", value: "Show" },
    { display: "Hide", value: "Hide" },
  ];

  return (
    <header className="relative h-16 flex items-center px-4 py-3 border-b shadow-sm">
      {/* Left Section */}
      <div className="flex items-center w-1/3">
        {isHomePage ? (
          <div className="p-1 flex flex-row items-center">
            <Cards size={25} className="mr-2" />
            <p>flashcards</p>
          </div>
        ) : (
          <div className="p-1">
            <House
              size={24}
              className="cursor-pointer"
              onClick={() => navigate("/")}
            />
          </div>
        )}
      </div>

      {/* Center Section */}
      <div className="flex-1 text-center font-medium">
        <button
          id="subject-select"
          onClick={() => navigate("/subjects")}
          className="border-black border-2 rounded"
        >
          <div className="flex-row flex items-center justify-center gap-1 p-2 bg-white cursor-pointer">
            <Books size={25} />
            {selectedSubject?.name || "Create subject"}
          </div>
        </button>
      </div>

      {/* Right Section */}
      <div className="flex justify-end w-1/3">
        {isRevising && (
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
        )}
      </div>

      {/* Dropdown */}
      {showDropdown && (
        <div className="absolute right-4 top-full mt-2 w-100 bg-white border-2 rounded z-10 text-sm p-3 space-y-2">
          <div className="flex items-center justify-between">
            <label>Card display:</label>
            <Toggle
              options={cardOptions}
              selected={cardDisplay}
              onChange={setCardDisplay}
            />
          </div>
          <div className="flex items-center justify-between">
            <label>Familiarity:</label>
            <Toggle
              options={familiarityOptions}
              selected={familiarity}
              onChange={setFamiliarity}
            />
          </div>
          <div className="flex items-center justify-between">
            <label>Show decks:</label>
            <Toggle
              options={deckOptions}
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
