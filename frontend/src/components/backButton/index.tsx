import { CaretLeft } from "phosphor-react";
import { useLocation, useNavigate } from "react-router-dom";

interface BackButtonProps {
  onClick?: () => void;
}

const BackButton = ({ onClick }: BackButtonProps) => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleClick = () => {
    if (onClick) {
      onClick();
    } else if (location.state?.returnToRevise) {
      const { cardId, deckId, hardMode } = location.state;
      navigate(`/revise${deckId ? `?deckId=${deckId}` : ""}`, {
        state: { frontCardId: cardId, hardMode },
      });
    } else {
      navigate(-1);
    }
  };

  return (
    <div className="relative left-0">
      <button
        id="decks-back-button"
        className="cursor-pointer"
        onClick={handleClick}
      >
        <CaretLeft size={24} />
      </button>
    </div>
  );
};

export default BackButton;
