import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import AddCard from "../pages/AddCard";
import Decks from "../pages/Decks";
import DeckDetails from "../pages/DeckDetails";
import ReviseWrapper from "../components/reviseWrapper";
import BulkUpload from "../pages/BulkUpload";
import Login from "../pages/Login";
import SubjectsPage from "../pages/Subjects";

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Home />} />
      <Route path="/revise/:deckId?" element={<ReviseWrapper />} />
      <Route path="/decks" element={<Decks />} />
      <Route path="/decks/:deckId" element={<DeckDetails />} />
      <Route path="/add-card/:cardId?" element={<AddCard />} />
      <Route path="/upload" element={<BulkUpload />} />
      <Route path="/subjects" element={<SubjectsPage />} />
    </Routes>
  );
};
