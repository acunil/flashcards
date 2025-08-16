import { useParams, useSearchParams } from "react-router-dom";
import Revise from "../../pages/Revise";

const ReviseWrapper = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const effectiveDeckId = deckId === "all" ? undefined : deckId;
  const [searchParams] = useSearchParams();
  const hardMode = searchParams.get("hardMode") === "true";

  return <Revise deckId={effectiveDeckId} hardMode={hardMode} />;
};

export default ReviseWrapper;
