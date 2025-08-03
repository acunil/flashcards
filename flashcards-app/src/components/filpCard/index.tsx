import { useState } from "react";

const FlipCard = () => {
  const [flipped, setFlipped] = useState(false);

  return (
    <div
      className="w-72 h-48 perspective cursor-pointer"
      onClick={() => setFlipped((prev) => !prev)}
    >
      <div
        className={`relative w-full h-full transition-transform duration-500 transform-style preserve-3d ${
          flipped ? "rotate-y-180" : ""
        }`}
      >
        <div className="absolute w-full h-full flex items-center justify-center bg-white border rounded-xl backface-hidden">
          Question Side
        </div>
        <div className="absolute w-full h-full flex items-center justify-center bg-gray-100 border rounded-xl rotate-y-180 backface-hidden">
          Answer Side
        </div>
      </div>
    </div>
  );
};

export default FlipCard;
