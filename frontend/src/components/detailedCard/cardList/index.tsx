import { useState } from "react";
import type { Card } from "../../../types/card";
import CardListItem from "../cardListItem";
import { Trash } from "phosphor-react";
import useUpdateCard from "../../../hooks/cards/useUpdateCard"; // assuming you have this
import useDeleteCards from "../../../hooks/cards/useDeleteCards";

interface CardListProps {
  cards: Card[];
  isAllCardsList?: boolean;
}

const CardList = ({ cards, isAllCardsList }: CardListProps) => {
  const { updateCard } = useUpdateCard();
  const { deleteCards } = useDeleteCards();
  const [bulkSelectMode, setBulkSelectMode] = useState(false);
  const [selectedCards, setSelectedCards] = useState<Set<number>>(new Set());
  const [searchQuery, setSearchQuery] = useState("");
  const [isEditing, setIsEditing] = useState(false);

  const toggleBulkSelectMode = () => {
    if (isEditing) return;
    setBulkSelectMode(true);
    setSelectedCards(new Set());
  };

  const handleDeleteSelected = () => {
    console.log("Deleting cards:", Array.from(selectedCards));

    if (isAllCardsList) {
      deleteCards(Array.from(selectedCards));
    }

    setSelectedCards(new Set());
    setBulkSelectMode(false);
  };

  const toggleCardSelection = (id: number) => {
    setSelectedCards((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(id)) newSet.delete(id);
      else newSet.add(id);
      if (newSet.size === 0) setBulkSelectMode(false);
      return newSet;
    });
  };

  const saveCard = async (updated: {
    id: number;
    front: string;
    back: string;
  }) => {
    updateCard({ ...updated, deckNames: [] });
  };

  // Filter cards based on search query
  const filteredCards = cards.filter(
    (card) =>
      card.front.toLowerCase().includes(searchQuery.toLowerCase()) ||
      card.back.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="max-w-xl w-full mx-auto relative">
      {/* Top controls */}
      <div className="flex justify-between mb-4 gap-2">
        {/* Search bar */}
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search cards..."
          className="flex-1 px-4 py-3 rounded shadow-lg border-2 border-black focus:outline-none"
        />

        {/* Bulk select / delete buttons */}
        {!bulkSelectMode ? (
          <button
            onClick={toggleBulkSelectMode}
            className={`${
              isEditing
                ? "disabled bg-gray-100 cursor-not-allowed"
                : "cursor-pointer bg-sky-200 hover:bg-sky-100"
            } flex items-center text-black py-3 px-4 rounded shadow-lg   border-black border-2 select-none`}
          >
            Select...
          </button>
        ) : (
          <button
            onClick={handleDeleteSelected}
            disabled={selectedCards.size === 0}
            className={`flex text-black py-3 px-4 rounded shadow-lg cursor-pointer bg-red-300 border-black border-2 items-center gap-2 ${
              selectedCards.size === 0 ? "opacity-50 cursor-not-allowed" : ""
            }`}
          >
            <Trash size={20} />
            {isAllCardsList ? (
              <span>Delete {selectedCards.size} cards</span>
            ) : (
              <span>Remove {selectedCards.size} from deck</span>
            )}
          </button>
        )}
      </div>

      {/* Card list */}
      <div className="flex flex-col gap-4 relative">
        {filteredCards.map((card) => (
          <div key={card.id} className="relative group">
            {bulkSelectMode && (
              <div
                className={`absolute top-2 left-2 w-6 h-6 rounded-full border-2 border-gray-400 flex items-center justify-center transition-all duration-100
      ${
        selectedCards.has(card.id)
          ? "bg-red-300 border-red-400"
          : "bg-white group-hover:bg-gray-100"
      }`}
              >
                {selectedCards.has(card.id) && (
                  <div className="w-3 h-3 bg-white rounded-full" />
                )}
              </div>
            )}
            <CardListItem
              {...card}
              onUpdate={saveCard}
              isSelectMode={bulkSelectMode}
              selected={selectedCards.has(card.id)}
              onToggleSelect={toggleCardSelection}
              onEditingChange={setIsEditing}
              isAllCardsList={isAllCardsList}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default CardList;
