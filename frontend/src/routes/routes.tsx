import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import AddCard from "../pages/AddCard";
import Decks from "../pages/Decks";
import DeckDetails from "../pages/DeckDetails";
import ReviseWrapper from "../components/reviseWrapper";
import BulkUpload from "../pages/BulkUpload";
import SubjectsPage from "../pages/Subjects";
import LandingWrapper from "../components/LandingWrapper";
import UserStats from "../pages/UserStats";
import MultipleChoice from "../pages/MultipleChoice";

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<LandingWrapper />} />
      <Route path="/home" element={<Home />} />
      <Route path="/revise/:deckId?" element={<ReviseWrapper />} />
      <Route path="/multiple-choice" element={<MultipleChoice />} />
      <Route path="/decks" element={<Decks />} />
      <Route path="/decks/:deckId" element={<DeckDetails />} />
      <Route path="/add-card/:cardId?" element={<AddCard />} />
      <Route path="/upload" element={<BulkUpload />} />
      <Route path="/subjects" element={<SubjectsPage />} />
      <Route path="/user-stats" element={<UserStats />} />
    </Routes>
  );
};
