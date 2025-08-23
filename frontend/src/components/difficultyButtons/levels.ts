import {
  Lightning,
  Smiley,
  SmileyMeh,
  SmileyNervous,
  SmileyXEyes,
} from "phosphor-react";

export const levels = [
  {
    label: "Impossible",
    Icon: SmileyXEyes,
    buttonClassName: "bg-red-300 hover:bg-red-400",
    rating: 5,
    color: "#DC2626",
  },
  {
    label: "Very Hard",
    Icon: SmileyNervous,
    buttonClassName: "bg-orange-300 hover:bg-orange-400",
    color: "#F97316",
    rating: 4,
  },
  {
    label: "Hard",
    Icon: SmileyMeh,
    buttonClassName: "bg-yellow-200 hover:bg-yellow-300",
    rating: 3,
    color: "#EAB308",
  },
  {
    label: "Medium",
    Icon: Smiley,
    buttonClassName: "bg-green-200 hover:bg-green-300",
    rating: 2,
    color: "#16A34A",
  },
  {
    label: "Easy",
    Icon: Lightning,
    buttonClassName: "bg-sky-200 hover:bg-sky-300",
    rating: 1,
    color: "#0284C7",
  },
];

export const getClosestLevel = (rating: number) => {
  // Find level with closest rating (round or nearest)
  let closest = levels[0];
  let minDiff = Math.abs(rating - closest.rating);

  for (const level of levels) {
    const diff = Math.abs(rating - level.rating);
    if (diff < minDiff) {
      closest = level;
      minDiff = diff;
    }
  }
  return closest;
};
