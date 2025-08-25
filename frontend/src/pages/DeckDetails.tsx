import { useNavigate, useParams } from "react-router-dom";
import Header from "../components/header";
import { GraduationCap, Pencil, FloppyDisk, X, Plus } from "phosphor-react";
import CardList from "../components/detailedCard/cardList";
import { useAppContext } from "../contexts";
import { useEffect, useMemo, useState } from "react";
import { useUpdateDeck } from "../hooks/decks";
import PageWrapper from "../components/pageWrapper";
import ContentWrapper from "../components/contentWrapper";
import BackButton from "../components/backButton";

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
    <PageWrapper className="bg-sky-200 min-h-screen">
      <Header />
      <ContentWrapper>
        <div className="flex flex-col w-full mb-4 gap-2">
          {/* Top row: Back button + centered heading */}
          <div className="flex items-center w-full">
            {/* Back button always left */}
            <div className="flex-shrink-0">
              <BackButton />
            </div>

            {/* Heading / edit input centered */}
            <div className="flex-1 flex justify-center">
              {effectiveDeckId === 0 ? (
                <h1 className="text-xl font-bold truncate max-w-full">
                  All cards
                </h1>
              ) : isEditing ? (
                <div className="flex items-center gap-2 max-w-full">
                  <input
                    type="text"
                    value={deckName}
                    onChange={(e) => setDeckName(e.target.value)}
                    onKeyDown={handleKeyDown}
                    className="border-b border-gray-400 text-center text-xl font-bold focus:outline-none focus:border-black flex-1 truncate max-w-full"
                    autoFocus
                  />
                  <div className="flex gap-1 flex-shrink-0">
                    <button
                      onClick={handleSave}
                      className="hover:text-green-700"
                    >
                      <FloppyDisk size={20} />
                    </button>
                    <button
                      onClick={() => {
                        setDeckName(deck?.name || "");
                        setIsEditing(false);
                      }}
                      className="hover:text-red-500"
                    >
                      <X size={20} />
                    </button>
                  </div>
                </div>
              ) : (
                <div className="flex items-center gap-2 max-w-full">
                  <h1 className="text-xl font-bold truncate">
                    Deck: {deck?.name}
                  </h1>
                  {deck && (
                    <button
                      onClick={() => setIsEditing(true)}
                      className="text-gray-500 hover:text-black flex-shrink-0"
                    >
                      <Pencil size={18} />
                    </button>
                  )}
                </div>
              )}
            </div>
          </div>

          {/* Action buttons: below heading on small screens */}
          <div className="flex gap-2 mt-2 flex-wrap justify-center sm:justify-end">
            <button
              onClick={handleClickAddCard}
              className="flex items-center justify-center p-2 bg-yellow-200 hover:bg-yellow-300 border-black border-2 rounded shadow"
            >
              <Plus size={20} />
            </button>
            <button
              onClick={handleClickRevise}
              className="flex items-center justify-center p-2 bg-pink-200 hover:bg-pink-300 border-black border-2 rounded shadow"
            >
              <GraduationCap size={20} />
            </button>
          </div>
        </div>

        {/* Card list */}
        <CardList
          cards={filteredCards}
          isAllCardsList={effectiveDeckId === 0}
          deckId={effectiveDeckId}
        />
      </ContentWrapper>
    </PageWrapper>
  );
};

export default DeckDetails;
