import { useState, useRef, useEffect } from "react";
import type { Deck } from "../../types/deck";
import { XCircle } from "phosphor-react";
import { useAppContext } from "../../contexts";

interface SearchableMultiSelectProps {
  options: Deck[];
  selected: Deck[];
  onChange: (decks: Deck[]) => void;
}

const SearchableMultiSelect = ({
  options,
  selected,
  onChange,
}: SearchableMultiSelectProps) => {
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const { selectedSubjectId } = useAppContext();

  const toggleDeck = (deck: Deck) => {
    if (selected.some((d) => d.id === deck.id)) {
      onChange(selected.filter((d) => d.id !== deck.id));
    } else {
      onChange([...selected, deck]);
    }
    setQuery("");
  };

  const addNewDeck = () => {
    const name = query.trim();
    if (!name) return;

    const allDecks = [...options, ...selected];
    const maxId =
      allDecks.length > 0 ? Math.max(...allDecks.map((d) => d.id)) : 0;

    const newDeck: Deck = {
      id: maxId + 1,
      name,
      subjectId: selectedSubjectId || 0,
    };

    if (!selected.some((d) => d.name.toLowerCase() === name.toLowerCase())) {
      onChange([...selected, newDeck]);
    }

    setQuery("");
    setOpen(false);
  };

  const filteredOptions = options.filter((deck) =>
    deck.name.toLowerCase().includes(query.toLowerCase())
  );

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="space-y-2" ref={containerRef}>
      {/* Selected decks */}
      <div className="flex flex-wrap gap-2">
        {selected.map((deck) => (
          <span
            key={deck.id}
            className="flex items-center gap-1 bg-sky-200 text-black px-3 py-1 rounded-full text-md border-none border-black"
          >
            {deck.name}
            <button
              type="button"
              onClick={() => toggleDeck(deck)}
              className="hover:text-red-500 hover:cursor-pointer"
            >
              <XCircle size={19} />
            </button>
          </span>
        ))}
      </div>

      {/* Search input */}
      <div className="relative">
        <input
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            setOpen(true);
          }}
          onFocus={() => setOpen(true)}
          placeholder="Search or add decks..."
          className="w-full p-2 border-2 rounded-md"
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              e.stopPropagation();
              addNewDeck();
            }
          }}
        />

        {/* Dropdown */}
        {open && (
          <div className="absolute z-10 mt-1 w-full bg-white border-2 border-black rounded shadow max-h-64 overflow-y-auto">
            {filteredOptions.length === 0 ? (
              <p className="p-2 text-sm text-gray-500">No results</p>
            ) : (
              filteredOptions.map((deck) => (
                <div
                  key={deck.id}
                  onClick={() => toggleDeck(deck)}
                  className={`p-2 cursor-pointer hover:bg-yellow-100 flex justify-between ${
                    selected.some((d) => d.id === deck.id) ? "bg-green-200" : ""
                  }`}
                >
                  <span>{deck.name}</span>
                  {selected.some((d) => d.id === deck.id) && <span>✔</span>}
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchableMultiSelect;
