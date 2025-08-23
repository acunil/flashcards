import { CaretLeft } from "phosphor-react";
import Header from "../components/header";
import { useNavigate } from "react-router-dom";

const SubjectsPage = () => {
  const navigate = useNavigate();
  return (
    <div className="bg-yellow-200 min-h-screen">
      <Header />
      <div className="flex justify-center">
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-4 rounded m-4">
          {/* Header with Back Button */}
          <div className="relative flex items-center h-12 mb-4">
            <div className="absolute left-0">
              <button
                id="decks-back-button"
                className="cursor-pointer"
                onClick={() => navigate("/decks")}
              >
                <CaretLeft size={24} />
              </button>
            </div>
            <h1 className="text-xl font-bold text-center mx-auto">Subjects</h1>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SubjectsPage;
