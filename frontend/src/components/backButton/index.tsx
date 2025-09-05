import { CaretLeft } from "phosphor-react";
import { useLocation, useNavigate } from "react-router-dom";

interface BackButtonProps {
  onClick?: () => void;
  returnState?: unknown; // optional state to pass to navigate
  returnPath?: string; // optional path to navigate to
}

const BackButton = ({ onClick, returnState, returnPath }: BackButtonProps) => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleClick = () => {
    if (onClick) {
      onClick();
    } else if (returnPath) {
      navigate(returnPath, { state: returnState });
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
    <div>
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
