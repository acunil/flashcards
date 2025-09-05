import { useState } from "react";
import type { Card } from "../../../types/card";
import CardListItem from "../cardListItem";
import { Trash } from "phosphor-react";
import useUpdateCard from "../../../hooks/cards/useUpdateCard"; // assuming you have this
import useDeleteCards from "../../../hooks/cards/useDeleteCards";
import { useAppContext } from "../../../contexts";
import useRemoveCardsFromDeck from "../../../hooks/decks/useRemoveCardsFromDeck";

interface CardListProps {
  cards: Card[];
  isAllCardsList?: boolean;
  deckId: number;
  searchQuery: string;
  setSearchQuery: (q: string) => void;
}

const CardList = ({
  cards,
  isAllCardsList,
  deckId,
  searchQuery,
  setSearchQuery,
}: CardListProps) => {
  const { updateCard } = useUpdateCard();
  const { deleteCards } = useDeleteCards();
  const { removeCards } = useRemoveCardsFromDeck();
  const [bulkSelectMode, setBulkSelectMode] = useState(false);
  const [selectedCards, setSelectedCards] = useState<Set<number>>(new Set());
  // const [searchQuery, setSearchQuery] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const { selectedSubjectId } = useAppContext();

  const toggleBulkSelectMode = () => {
    if (isEditing) return;
    setBulkSelectMode(true);
    setSelectedCards(new Set());
  };

  const handleDeleteSelected = () => {
    console.log("Deleting cards:", Array.from(selectedCards));

    if (isAllCardsList) {
      deleteCards(Array.from(selectedCards));
    } else {
      removeCards(Array.from(selectedCards), deckId);
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
    updateCard({
      ...updated,
      deckNames: [],
      subjectId: selectedSubjectId || 0,
    });
  };

  // Filter cards based on search query
  const filteredCards = cards.filter(
    (card) =>
      card.front.toLowerCase().includes(searchQuery.toLowerCase()) ||
      card.back.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="w-full mx-auto relative">
      {/* Top controls */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-2 mb-4">
        {/* Search bar */}
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search cards..."
          className="w-full sm:flex-1 px-4 py-3 rounded shadow-lg border-2 border-black focus:outline-none"
        />

        {/* Bulk select / delete buttons */}
        {!bulkSelectMode ? (
          <button
            onClick={toggleBulkSelectMode}
            className={`w-full sm:w-auto mt-2 sm:mt-0 ${
              isEditing
                ? "disabled bg-gray-100 cursor-not-allowed"
                : "cursor-pointer bg-sky-200 hover:bg-sky-100"
            } flex items-center justify-center text-black py-3 px-4 rounded shadow-lg border-black border-2`}
          >
            Select...
          </button>
        ) : (
          <button
            onClick={handleDeleteSelected}
            disabled={selectedCards.size === 0}
            className={`w-full sm:w-auto mt-2 sm:mt-0 flex text-black py-3 px-4 rounded shadow-lg cursor-pointer bg-red-300 border-black border-2 items-center justify-center gap-2 ${
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
      <div className="flex flex-col gap-4">
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
              searchQuery={searchQuery}
              deckId={deckId}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default CardList;
