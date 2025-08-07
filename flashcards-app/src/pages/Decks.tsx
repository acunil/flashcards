import { useNavigate } from "react-router-dom";
import DeckList from "../components/deck/deckList";
import Header from "../components/header";
import { CaretLeft } from "phosphor-react";

const Decks = () => {
  const navigate = useNavigate();
  const decks = ["family", "job words", "other"];
  return (
    <div className="bg-sky-200">
      <Header />
      <div className="min-h-screen flex justify-center">
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-3 rounded m-2">
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
          <DeckList decks={decks} />
        </div>
      </div>
    </div>
  );
};

export default Decks;
