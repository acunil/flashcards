import { useNavigate, useParams } from "react-router-dom";
import Header from "../components/header";
import {
  CaretLeft,
  GraduationCap,
  Pencil,
  FloppyDisk,
  X,
  Plus,
} from "phosphor-react";
import CardList from "../components/detailedCard/cardList";
import { useAppContext } from "../contexts";
import { useEffect, useMemo, useState } from "react";
import { useUpdateDeck } from "../hooks/decks";

const DeckDetails = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const effectiveDeckId = deckId === "all" ? 0 : Number(deckId);
  const { decks, cards, setDecks } = useAppContext();
  const navigate = useNavigate();
  const { updateDeckName } = useUpdateDeck();

  const deck = effectiveDeckId
    ? decks.find((d) => d.id === effectiveDeckId)
    : null;

  // Optimistic state for editing
  const [isEditing, setIsEditing] = useState(false);
  const [deckName, setDeckName] = useState(deck?.name || "");

  // Sync deck name when deck changes
  useEffect(() => {
    if (deck) setDeckName(deck.name);
  }, [deck]);

  // Filter cards for this deck
  const filteredCards = useMemo(() => {
    if (effectiveDeckId === 0 || !deckId) return cards;
    return cards.filter((card) =>
      card.decks.some((d) => d.id === effectiveDeckId)
    );
  }, [cards, effectiveDeckId, deckId]);

  // Revise button handler
  const handleClickRevise = () => {
    navigate(
      deckId === "all"
        ? `/revise/?hardMode=false`
        : `/revise/${deckId}?hardMode=false`
    );
  };

  // Optimistic save
  const handleSave = () => {
    if (!deck || deckName.trim() === "") return;

    // Optimistically update context
    setDecks((prev) =>
      prev.map((d) => (d.id === deck.id ? { ...d, name: deckName } : d))
    );

    updateDeckName(deck.id, deckName).catch((err) => {
      console.error("Failed to update deck name", err);
      // Revert if failed
      setDecks((prev) =>
        prev.map((d) => (d.id === deck.id ? { ...d, name: deck.name } : d))
      );
    });

    setIsEditing(false);
  };

  // Handle keyboard interactions
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleSave();
    if (e.key === "Escape") {
      setDeckName(deck?.name || "");
      setIsEditing(false);
    }
  };

  const handleClickAddCard = () => {
    navigate(`/add-card?deckId=${deck?.id}`);
  };

  return (
    <div className="bg-sky-200 min-h-screen">
      <Header />
      <div className="flex justify-center py-4">
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-4 rounded">
          {/* Header */}
          <div className="relative flex items-center h-12 mb-4">
            <button
              onClick={() => navigate("/decks")}
              className="absolute left-0 p-1 hover:bg-gray-200 rounded"
            >
              <CaretLeft size={24} />
            </button>

            <div className="mx-auto flex items-center gap-2">
              {effectiveDeckId === 0 ? (
                <h1 className="text-xl font-bold text-center">
                  <div className="flex flex-row items-center gap-2">
                    All cards
                  </div>
                </h1>
              ) : isEditing ? (
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    value={deckName}
                    onChange={(e) => setDeckName(e.target.value)}
                    onKeyDown={handleKeyDown}
                    className="border-b border-gray-400 text-center text-xl font-bold focus:outline-none focus:border-black"
                    autoFocus
                  />
                  <button
                    onClick={handleSave}
                    className="hover:text-green-700 cursor-pointer"
                  >
                    <FloppyDisk size={20} />
                  </button>
                  <button
                    onClick={() => {
                      setDeckName(deck?.name || "");
                      setIsEditing(false);
                    }}
                    className="hover:text-red-500 cursor-pointer"
                  >
                    <X size={20} />
                  </button>
                </div>
              ) : (
                <div className="flex items-center gap-2">
                  <h1 className="text-xl font-bold text-center">
                    <div className="flex flex-row items-center gap-2">
                      Deck: {deck?.name}
                    </div>
                  </h1>
                  {deck && (
                    <button
                      onClick={() => setIsEditing(true)}
                      className="text-gray-500 hover:text-black cursor-pointer"
                    >
                      <Pencil size={18} />
                    </button>
                  )}
                </div>
              )}
            </div>

            <div className="flex flex-row gap-2 justify-end">
              <button
                onClick={handleClickAddCard}
                className="flex items-center cursor-pointer justify-center p-2 bg-yellow-200 hover:bg-yellow-300 border-black border-2 rounded shadow"
              >
                <Plus size={20} />
              </button>
              <button
                onClick={handleClickRevise}
                className="flex items-center cursor-pointer justify-center p-2 bg-pink-200 hover:bg-pink-300 border-black border-2 rounded shadow"
              >
                <GraduationCap size={20} />
              </button>
            </div>
          </div>

          {/* Card list */}
          <CardList
            cards={filteredCards}
            isAllCardsList={effectiveDeckId === 0}
          />
        </div>
      </div>
    </div>
  );
};

export default DeckDetails;
