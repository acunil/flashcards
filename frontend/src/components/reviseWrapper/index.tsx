import { useParams, useSearchParams } from "react-router-dom";
import Revise from "../../pages/Revise";

const ReviseWrapper = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const effectiveDeckId = deckId === "0" ? undefined : Number(deckId);
  const [searchParams] = useSearchParams();
  const hardMode = searchParams.get("hardMode") === "true";

  return <Revise deckId={effectiveDeckId} hardMode={hardMode} />;
};

export default ReviseWrapper;
