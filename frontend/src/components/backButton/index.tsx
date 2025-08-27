import { CaretLeft } from "phosphor-react";
import { useNavigate } from "react-router-dom";

const BackButton = () => {
  const navigate = useNavigate();
  return (
    <div className="relative left-0">
      <button
        id="decks-back-button"
        className="cursor-pointer"
        onClick={() => navigate(-1)}
      >
        <CaretLeft size={24} />
      </button>
    </div>
  );
};

export default BackButton;
