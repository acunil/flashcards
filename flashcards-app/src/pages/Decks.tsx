import { useState, useEffect } from "react";
import useAllDecks from "../hooks/decks/useAllDecks";
import useCreateDeck from "../hooks/decks/useCreateDeck";
import DeckList, { type Deck } from "../components/deck/deckList";
import Header from "../components/header";
import { CaretLeft } from "phosphor-react";
import { useNavigate } from "react-router-dom";
import DeckListSkeleton from "../components/deck/deckList/deckListSkeleton";

const Decks = () => {
  const navigate = useNavigate();

  const { decks: fetchedDecks, loading, error } = useAllDecks();
  const { createDeck, loading: creating, error: createError } = useCreateDeck();

  // Local decks state to immediately update UI when adding decks
  const [decks, setDecks] = useState<Deck[]>([]);

  // Sync fetched decks to local state
  useEffect(() => {
    setDecks(fetchedDecks);
  }, [fetchedDecks]);

  const handleAddDeck = async (name: string) => {
    if (!name.trim()) return;

    const newDeck = await createDeck(name.trim());
    if (newDeck) {
      setDecks((prev) => [...prev, newDeck]);
    }
  };

  return (
    <div className="bg-sky-200">
      <Header />
      <div className="min-h-screen flex justify-center">
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-3 rounded m-4">
          <div className="relative flex items-center h-12">
            <div className="absolute left-0">
              <button
                id="decks-back-button"
                className="cursor-pointer"
                onClick={() => navigate("/")}
              >
                <CaretLeft size={24} />
              </button>
            </div>
            <p className="mx-auto text-md">decks</p>
          </div>

          {(loading || creating) && <DeckListSkeleton />}
          {(error || createError) && (
            <p className="text-red-600">Error: {error || createError}</p>
          )}

          {!loading && !creating && !error && !createError && (
            <DeckList decks={decks} onAddDeck={handleAddDeck} />
          )}
        </div>
      </div>
    </div>
  );
};

export default Decks;
