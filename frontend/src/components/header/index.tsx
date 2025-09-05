import { Books, Cards, Gear, House, UserCircle } from "phosphor-react";
import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Toggle, { type ToggleOption } from "../toggle";
import { useAppContext } from "../../contexts";
import { useReviseSettings } from "../../hooks/reviseSettings";
import type {
  CardDisplay,
  CardOrder,
  DeckVisibility,
  Familiarity,
} from "../../contexts/ReviseSettingsContext";
import { useAuth0 } from "@auth0/auth0-react";
import useUpdateSubject from "../../hooks/subjects/useUpdateSubject";

interface HeaderProps {
  isHomePage?: boolean;
  isRevising?: boolean;
  isErrorMode?: boolean;
}

const Header = ({
  isHomePage = false,
  isRevising = false,
  isErrorMode = false,
}: HeaderProps) => {
  const navigate = useNavigate();
  const [showRevisionDropdown, setShowRevisionDropdown] = useState(false);
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const { selectedSubject } = useAppContext();
  const { logout } = useAuth0();
  const { updateSubject } = useUpdateSubject();

  const {
    cardDisplay,
    setCardDisplay,
    familiarity,
    setFamiliarity,
    showDeckNames,
    setShowDeckNames,
    cardOrder,
    setCardOrder,
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

  const cardOrderOptions: ToggleOption<CardOrder>[] = [
    { display: "Newest", value: "Newest" },
    { display: "Oldest", value: "Oldest" },
    { display: "Random", value: "Shuffle" },
  ];

  const persistDefaultSide = async (value: CardDisplay) => {
    // Optimistic UI
    setCardDisplay(value);

    if (!selectedSubject?.id) return; // no subject yet

    try {
      await updateSubject({
        id: selectedSubject.id,
        name: selectedSubject.name,
        frontLabel: selectedSubject.frontLabel || "Front",
        backLabel: selectedSubject.backLabel || "Back",
        defaultSide: value.toUpperCase(),
      });
    } catch (err: unknown) {
      if (err instanceof Error) {
        console.log(err.message);
      } else {
        console.log(String(err));
      }
    }
  };

  const persistDeckVisibility = async (value: DeckVisibility) => {
    // Optimistic UI
    setShowDeckNames(value);

    if (!selectedSubject?.id) return;

    try {
      await updateSubject({
        id: selectedSubject.id,
        name: selectedSubject.name,
        frontLabel: selectedSubject.frontLabel || "Front",
        backLabel: selectedSubject.backLabel || "Back",
        displayDeckNames: value === "Show",
      });
    } catch (err: unknown) {
      if (err instanceof Error) {
        console.log(err.message);
      } else {
        console.log(String(err));
      }
    }
  };

  const persistCardOrder = async (value: CardOrder) => {
    setCardOrder(value);

    // TODO: add logic for updating preference in database subject record
    // if (!selectedSubject?.id) return;
  };

  const handleLogout = () => {
    localStorage.removeItem("selectedSubjectId");
    logout({ logoutParams: { returnTo: window.location.origin } });
  };

  // Separate refs for buttons and dropdowns
  const revisionButtonRef = useRef<HTMLDivElement>(null);
  const revisionDropdownRef = useRef<HTMLDivElement>(null);
  const userButtonRef = useRef<HTMLDivElement>(null);
  const userDropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      // Revision dropdown
      if (
        showRevisionDropdown &&
        revisionDropdownRef.current &&
        revisionButtonRef.current &&
        !revisionDropdownRef.current.contains(event.target as Node) &&
        !revisionButtonRef.current.contains(event.target as Node)
      ) {
        setShowRevisionDropdown(false);
      }
      // User dropdown
      if (
        showUserDropdown &&
        userDropdownRef.current &&
        userButtonRef.current &&
        !userDropdownRef.current.contains(event.target as Node) &&
        !userButtonRef.current.contains(event.target as Node)
      ) {
        setShowUserDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [showRevisionDropdown, showUserDropdown]);

  return (
    <header className="relative h-16 flex items-center justify-between px-3 sm:px-4 border-b shadow-sm">
      {/* Left Section */}
      <div className="flex items-center flex-1">
        {isHomePage ? (
          <div className="p-1 flex flex-row items-center">
            <Cards size={22} className="mr-1 sm:mr-2" />
            <p className="text-sm sm:text-base">flashcards</p>
          </div>
        ) : (
          <div className="p-1">
            <House
              size={22}
              className={`${!isErrorMode ? "cursor-pointer" : ""}`}
              onClick={isErrorMode ? () => {} : () => navigate("/")}
            />
          </div>
        )}
      </div>

      {/* Center Section (Subject) */}
      {!isErrorMode && (
        <div className="px-2">
          <button
            id="subject-select"
            onClick={() => navigate("/subjects")}
            className="border-2 border-black rounded w-fit max-w-[250px] sm:max-w-[300px] md:max-w-[400px] truncate bg-white"
          >
            <div className="flex flex-row items-center justify-center gap-1 px-2 py-1 cursor-pointer">
              <Books size={20} />
              <span className="truncate">
                {selectedSubject?.name || "Create subject"}
              </span>
            </div>
          </button>
        </div>
      )}

      {/* Right Section (User + Settings) */}
      <div className="flex items-center gap-2 flex-1 justify-end">
        {isRevising && (
          <div
            ref={revisionButtonRef}
            className={`p-1 border-2 rounded transition-colors cursor-pointer ${
              showRevisionDropdown
                ? "bg-white border-black"
                : "border-transparent"
            }`}
            onClick={() => setShowRevisionDropdown((prev) => !prev)}
          >
            <Gear size={22} />
          </div>
        )}

        <div
          ref={userButtonRef}
          className={`p-1 border-2 rounded transition-colors cursor-pointer ${
            showUserDropdown ? "bg-white border-black" : "border-transparent"
          }`}
          onClick={() => setShowUserDropdown((prev) => !prev)}
        >
          <UserCircle size={22} />
        </div>
      </div>

      {/* Revision Dropdown */}
      {showRevisionDropdown && (
        <div
          ref={revisionDropdownRef}
          className="absolute right-0 top-full mt-2 w-full mx-2 sm:w-[400px] bg-white border-2 rounded z-10 text-sm p-3 space-y-4 sm:space-y-3 shadow-lg max-w-[95vw] sm:right-3 sm:mt-2 sm:p-3"
        >
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
            <label className="whitespace-nowrap">Card display:</label>
            <Toggle
              options={cardOptions}
              selected={cardDisplay}
              onChange={persistDefaultSide}
            />
          </div>
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
            <label className="whitespace-nowrap">Familiarity:</label>
            <Toggle
              options={familiarityOptions}
              selected={familiarity}
              onChange={setFamiliarity}
            />
          </div>
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
            <label className="whitespace-nowrap">Show decks:</label>
            <Toggle
              options={deckOptions}
              selected={showDeckNames}
              onChange={persistDeckVisibility}
            />
          </div>
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
            <label className="whitespace-nowrap">Card order:</label>
            <Toggle
              options={cardOrderOptions}
              selected={cardOrder}
              onChange={persistCardOrder}
            />
          </div>
          <div className="flex justify-end pt-2">
            <button
              onClick={() => setShowRevisionDropdown(false)}
              className="border-2 bg-sky-200 px-4 py-2 rounded-md cursor-pointer hover:bg-sky-300 transition w-full sm:w-auto"
            >
              Done
            </button>
          </div>
        </div>
      )}

      {/* User Dropdown */}
      {showUserDropdown && (
        <div
          ref={userDropdownRef}
          className="absolute right-0 mx-2 top-full mt-2 w-32 bg-white border-2 rounded z-10 text-sm p-3 space-y-2"
        >
          <div className="flex justify-end">
            <button
              onClick={handleLogout}
              className="border-2 bg-sky-200 px-4 py-1 rounded-md cursor-pointer w-full hover:bg-sky-300 transition"
            >
              Logout
            </button>
          </div>
        </div>
      )}
    </header>
  );
};

export default Header;
