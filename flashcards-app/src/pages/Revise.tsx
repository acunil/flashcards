import DifficultyButtons from "../components/difficultyButtons";
import FlipCard from "../components/filpCard";
import Header from "../components/header";

const Revise = () => {
  return (
    <div className="min-h-screen">
      <Header />
      <main className="flex flex-col items-center gap-6 p-6">
        <FlipCard />
        <DifficultyButtons />
      </main>
    </div>
  );
};

export default Revise;
