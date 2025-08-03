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
    buttonClassName: "bg-red-300",
    rating: 5,
  },
  {
    label: "Very Hard",
    Icon: SmileyNervous,
    buttonClassName: "bg-orange-300",
    rating: 4,
  },
  {
    label: "Hard",
    Icon: SmileyMeh,
    buttonClassName: "bg-yellow-200",
    rating: 3,
  },
  { label: "Medium", Icon: Smiley, buttonClassName: "bg-green-200", rating: 2 },
  { label: "Easy", Icon: Lightning, buttonClassName: "bg-sky-200", rating: 1 },
];
