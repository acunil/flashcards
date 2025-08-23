import { useContext } from "react";
import {
  ReviseSettingsContext,
  type ReviseSettings,
} from "../../contexts/ReviseSettingsContext";

export const useReviseSettings = (): ReviseSettings => {
  const ctx = useContext(ReviseSettingsContext);
  if (!ctx)
    throw new Error(
      "useReviseSettings must be used within a ReviseSettingsProvider"
    );
  return ctx;
};
