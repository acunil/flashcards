import { Gear, House } from "phosphor-react";
import { useNavigate } from "react-router-dom";

const Header = () => {
  const navigate = useNavigate();

  return (
    <header className="flex justify-between items-center px-4 py-3 border-b shadow-sm">
      <House
        size={24}
        className="cursor-pointer"
        onClick={() => navigate("/")}
      />
      <Gear size={24} className="cursor-pointer" />
    </header>
  );
};

export default Header;
