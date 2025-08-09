import { useNavigate, useParams } from "react-router-dom";
import Header from "../components/header";
import { CaretLeft } from "phosphor-react";
import CardList from "../components/detailedCard/cardList";

const mockCards = [
  {
    id: "1",
    textFront: "Hello",
    textBack: "Hallo",
    viewCount: 10,
    avgRating: 3.5,
    lastRating: 4,
  },
  {
    id: "2",
    textFront: "Goodbye",
    textBack: "Auf Wiedersehen",
    viewCount: 5,
    avgRating: 2.1,
    lastRating: 1,
  },
  {
    id: "3",
    textFront: "Thank you",
    textBack: "Danke",
    viewCount: 12,
    avgRating: 4.9,
    lastRating: 5,
  },
  {
    id: "4",
    textFront: "Yes",
    textBack: "Ja",
    viewCount: 8,
    avgRating: 3.2,
    lastRating: 3,
  },
];

const DeckDetails = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const navigate = useNavigate();

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
            <p className="mx-auto text-md">my deck for deck ID: {deckId}</p>
          </div>
          <div className="mt-6 flex justify-center mx-auto w-full">
            <CardList cards={mockCards} />
          </div>
        </div>
      </div>
    </div>
  );
};
export default DeckDetails;
