import type { ReactNode } from "react";

interface HeadingProps {
  children: ReactNode;
}
export const Heading = ({ children }: HeadingProps) => {
  return <h1 className="text-xl font-bold text-center mx-auto">{children}</h1>;
};

export default Heading;
