import { CaretLeft } from "phosphor-react";
import { useNavigate } from "react-router-dom";

interface BackButtonProps {
  onClick?: () => void;
}

const BackButton = ({ onClick }: BackButtonProps) => {
  const navigate = useNavigate();

  const handleClick = () => {
    if (onClick) {
      onClick();
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
