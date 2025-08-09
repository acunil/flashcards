import { useState, useRef, useEffect } from "react";
import { KeyReturn, Plus } from "phosphor-react";

interface AddDeckButtonProps {
  onAddDeck: (name: string) => void;
}

const AddDeckButton = ({ onAddDeck }: AddDeckButtonProps) => {
  const [isEditing, setIsEditing] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);
  const buttonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (isEditing && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isEditing]);

  // Handle blur: when focus leaves button/input, stop editing
  const handleBlur = (
    e: React.FocusEvent<HTMLButtonElement | HTMLInputElement>
  ) => {
    // Check if the newly focused element is inside this button
    if (
      e.relatedTarget &&
      (buttonRef.current?.contains(e.relatedTarget as Node) ||
        inputRef.current?.contains(e.relatedTarget as Node))
    ) {
      return; // focus still inside, do nothing
    }
    setIsEditing(false);
    setInputValue("");
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && inputValue.trim() !== "") {
      onAddDeck(inputValue.trim());
      setInputValue("");
      setIsEditing(false);
    } else if (e.key === "Escape") {
      setInputValue("");
      setIsEditing(false);
    }
  };

  return (
    <button
      ref={buttonRef}
      onClick={() => setIsEditing(true)}
      onBlur={handleBlur}
      className={`flex items-center justify-center py-3 px-4 w-full rounded shadow-lg cursor-pointer border-black border-2 transition-colors duration-200
        ${
          isEditing
            ? "bg-white hover:bg-white"
            : "bg-gray-200 hover:bg-gray-300"
        }
      `}
      type="button"
    >
      {isEditing ? (
        <>
          <input
            ref={inputRef}
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={handleKeyDown}
            onClick={(e) => e.stopPropagation()}
            onBlur={handleBlur}
            placeholder="Enter deck name..."
            className="
          w-full bg-transparent outline-none text-black
          text-base leading-none
          border-none
          box-border
        "
          />
          <KeyReturn size={18} className="text-gray-400" />
        </>
      ) : (
        <Plus size={18} />
      )}
    </button>
  );
};

export default AddDeckButton;
