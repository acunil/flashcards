import { CaretLeft } from "phosphor-react";
import { useNavigate } from "react-router-dom";

const BackButton = () => {
  const navigate = useNavigate();
  return (
    <div className="absolute left-0">
      <button
        id="decks-back-button"
        className="cursor-pointer"
        onClick={() => navigate("/")}
      >
        <CaretLeft size={24} />
      </button>
    </div>
  );
};

export default BackButton;
