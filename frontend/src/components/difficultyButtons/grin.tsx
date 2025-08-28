import type { IconProps } from "phosphor-react";

interface GrinIconProps extends IconProps {
  weight?: "regular" | "duotone";
}

const GrinIcon = ({
  size = 20,
  color = "currentColor",
  weight = "regular",
}: GrinIconProps) => {
  const fillColor = weight === "duotone" ? "rgba(0, 212, 255, 0.2)" : "none";

  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 256 256"
      width={size}
      height={size}
    >
      {/* Light background only in duotone */}
      {weight === "duotone" && (
        <circle cx="128" cy="128" r="104" fill={fillColor} />
      )}

      {/* Original face paths */}
      <path
        d="M128,24A104,104,0,1,0,232,128,104.11,104.11,0,0,0,128,24Zm0,192a88,88,0,1,1,88-88A88.1,88.1,0,0,1,128,216ZM80,108a12,12,0,1,1,12,12A12,12,0,0,1,80,108Zm96,0a12,12,0,1,1-12-12A12,12,0,0,1,176,108Zm-92,40c13,22,34,34,44,34s31-12,44-34a8,8,0,1,1,14,8c-15,26-38,42-58,42s-43-16-58-42a8,8,0,0,1,14-8Z"
        fill={color}
      />

      {/* Smile line remains exactly as-is */}
      <line
        x1="87"
        y1="150"
        x2="169"
        y2="150"
        stroke={color}
        strokeWidth="16"
        strokeLinecap="round"
      />
    </svg>
  );
};

export default GrinIcon;
