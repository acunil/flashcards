import { useNavigate, useParams } from "react-router-dom";
import Header from "../components/header";
import { CaretLeft, GraduationCap } from "phosphor-react";
import CardList from "../components/detailedCard/cardList";
import { useAppContext } from "../contexts";
import { useMemo } from "react";

const DeckDetails = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const effectiveDeckId = Number(deckId);
  const { decks, cards } = useAppContext();
  const navigate = useNavigate();

  // if deck id is 0, show all cards
  // if deck id is not all, only include cards that have the deck id

  const filteredCards = useMemo(() => {
    if (effectiveDeckId === 0 || !deckId) {
      return cards;
    }
    const deckIdNum = Number(deckId);
    return cards.filter((card) =>
      card.decks.some((deck) => deck.id === deckIdNum)
    );
  }, [cards, effectiveDeckId, deckId]);

  const handleClickRevise = () => {
    if (deckId === "all") {
      navigate(`/revise/?hardMode=false`);
    } else {
      navigate(`/revise/${deckId}?hardMode=false`);
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
                onClick={() => navigate("/decks")}
              >
                <CaretLeft size={24} />
              </button>
            </div>
            <div className="absolute right-0">
              <button
                onClick={handleClickRevise}
                className="relative flex items-center text-black py-3 px-4 mr-5 mt-3 rounded shadow-lg cursor-pointer bg-yellow-200 hover:bg-yellow-100 border-black border-2"
              >
                <GraduationCap size={20} />
              </button>
            </div>
            <h1 className="text-xl font-bold text-center mx-auto">
              {effectiveDeckId
                ? decks.find((deck) => deck.id == effectiveDeckId)?.name
                : "all cards"}
            </h1>
          </div>
          <div className="mt-6 flex justify-center mx-auto w-full">
            <CardList cards={filteredCards} />
          </div>
        </div>
      </div>
    </div>
  );
};
export default DeckDetails;
