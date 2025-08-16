import { useContext } from "react";

export const useCardEdit = () => {
  const context = useContext(CardEditContext);
  if (!context) {
    throw new Error("useCardEdit must be used within a CardEditProvider");
  }
  return context;
};
