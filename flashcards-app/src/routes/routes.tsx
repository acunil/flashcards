import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import Revise from "../pages/Revise";
import AddCard from "../pages/AddCard";
import Decks from "../pages/Decks";

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/revise" element={<Revise />} />
      <Route path="/revise-hard" element={<Revise hardMode={true} />} />
      <Route path="/decks" element={<Decks />} />
      <Route path="/add-card" element={<AddCard />} />
    </Routes>
  );
};
