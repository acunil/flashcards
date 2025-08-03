import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import Revise from "../pages/Revise";

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/revise" element={<Revise />} />
      <Route path="/revise-hard" element={<Revise hardMode={true} />} />
    </Routes>
  );
};
