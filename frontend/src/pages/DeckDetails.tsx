import { useNavigate, useParams } from "react-router-dom";
import Header from "../components/header";
import { CaretLeft, GraduationCap, Pencil, FloppyDisk } from "phosphor-react";
import CardList from "../components/detailedCard/cardList";
import { useAppContext } from "../contexts";
import { useMemo, useState } from "react";
import { useUpdateDeck } from "../hooks/decks";

const DeckDetails = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const effectiveDeckId = deckId === "all" ? 0 : Number(deckId);
  const { decks, cards } = useAppContext(); // assume context provides updateDeckName
  const navigate = useNavigate();
  const { updateDeckName } = useUpdateDeck();

  const filteredCards = useMemo(() => {
    if (effectiveDeckId === 0 || !deckId) return cards;
    return cards.filter((card) =>
      card.decks.some((deck) => deck.id === effectiveDeckId)
    );
  }, [cards, effectiveDeckId, deckId]);

  const handleClickRevise = () => {
    if (deckId === "all") {
      navigate(`/revise/?hardMode=false`);
    } else {
      navigate(`/revise/${deckId}?hardMode=false`);
    }
  };

  const deck = effectiveDeckId
    ? decks.find((d) => d.id === effectiveDeckId)
    : null;
  const [isEditing, setIsEditing] = useState(false);
  const [deckName, setDeckName] = useState(deck?.name || "");

  const handleSave = () => {
    if (deck && deckName.trim() !== "") {
      updateDeckName(deck.id, deckName);
      setIsEditing(false);
    }
  };

  return (
    <div className="bg-sky-200">
      <Header />
      <div className="min-h-screen flex justify-center">
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-3 rounded m-4">
          <div className="relative flex items-center h-12">
            {/* Back button */}
            <div className="absolute left-0">
              <button
                id="decks-back-button"
                className="cursor-pointer"
                onClick={() => navigate("/decks")}
              >
                <CaretLeft size={24} />
              </button>
            </div>

            {/* Revise button */}
            <div className="absolute right-0">
              <button
                onClick={handleClickRevise}
                className="relative flex items-center text-black py-3 px-4 mr-5 mt-3 rounded shadow-lg cursor-pointer bg-yellow-200 hover:bg-yellow-100 border-black border-2"
              >
                <GraduationCap size={20} />
              </button>
            </div>

            {/* Deck name or All Cards */}
            <div className="mx-auto flex items-center gap-2">
              {effectiveDeckId === 0 ? (
                <h1 className="text-xl font-bold text-center">all cards</h1>
              ) : isEditing ? (
                <>
                  <input
                    type="text"
                    value={deckName}
                    onChange={(e) => setDeckName(e.target.value)}
                    className="border-gray-400 border-b text-center text-xl font-bold focus:outline-none focus:border-black"
                    autoFocus
                  />
                  <button
                    onClick={handleSave}
                    className="text-black hover:text-green-700 hover:cursor-pointer"
                  >
                    <FloppyDisk size={20} />
                  </button>
                </>
              ) : (
                <>
                  <h1 className="text-xl font-bold text-center">
                    {deck?.name}
                  </h1>
                  {deck && (
                    <button
                      onClick={() => {
                        setDeckName(deck.name);
                        setIsEditing(true);
                      }}
                      className="text-gray-500 hover:text-black hover:cursor-pointer"
                    >
                      <Pencil size={18} />
                    </button>
                  )}
                </>
              )}
            </div>
          </div>

          {/* Card list */}
          <div className="mt-6 flex justify-center mx-auto w-full">
            <CardList
              cards={filteredCards}
              isAllCardsList={effectiveDeckId === 0}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeckDetails;
