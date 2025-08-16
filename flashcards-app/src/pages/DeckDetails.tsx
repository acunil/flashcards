import { useNavigate, useParams } from "react-router-dom";
import Header from "../components/header";
import { CaretLeft, GraduationCap } from "phosphor-react";
import CardList from "../components/detailedCard/cardList";
import { useDeckContext } from "../contexts";

const DeckDetails = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const { decks } = useDeckContext();
  const navigate = useNavigate();

  let cards = [];
  let deckName = "";

  if (deckId === "all") {
    cards = Array.from(
      new Map(
        decks
          .flatMap((deck) => deck.cardResponses)
          .map((card) => [card.id, card])
      ).values()
    );
    deckName = "all cards";
  } else {
    const currentDeck = decks.find((d) => d.id == deckId);
    cards = currentDeck?.cardResponses || [];
    deckName = currentDeck?.name || "";
  }

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
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-3 rounded m-4 mx-auto">
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
            <p className="mx-auto text-md">{deckName}</p>
          </div>
          <div className="mt-6 flex justify-center mx-auto w-full">
            <CardList cards={cards} />
          </div>
        </div>
      </div>
    </div>
  );
};
export default DeckDetails;
