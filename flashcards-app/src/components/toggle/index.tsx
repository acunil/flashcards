import { useState } from "react";

interface ToggleProps<T extends string | number> {
  options: T[];
  selected?: T;
  onChange?: (value: T) => void;
}

function Toggle<T extends string | number>({
  options,
  selected,
  onChange,
}: ToggleProps<T>) {
  const [internalSelected, setInternalSelected] = useState<T>(options[0]);
  const isControlled = selected !== undefined;
  const current = isControlled ? selected : internalSelected;

  const handleSelect = (value: T) => {
    if (!isControlled) setInternalSelected(value);
    onChange?.(value);
  };

  return (
    <div className="flex border-2 rounded-md overflow-hidden divide-x divide-gray-300">
      {options.map((option, index) => {
        const isSelected = option === current;
        const rounded =
          index === 0
            ? "rounded-l-md"
            : index === options.length - 1
            ? "rounded-r-md"
            : "";

        return (
          <button
            key={option}
            onClick={() => handleSelect(option)}
            className={`px-4 py-2 text-sm font-medium transition cursor-pointer ${rounded} 
              ${isSelected ? "bg-yellow-200" : "bg-white hover:bg-gray-200"}
            `}
          >
            {option}
          </button>
        );
      })}
    </div>
  );
}

export default Toggle;
