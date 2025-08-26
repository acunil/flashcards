import type { ReactNode } from "react";

interface ContentWrapperProps {
  children: ReactNode;
}
const ContentWrapper = ({ children }: ContentWrapperProps) => {
  return (
    <div className="flex justify-center m-4">
      <div className="bg-white w-full max-w-screen-sm border-black border-2 p-4 rounded">
        {children}
      </div>
    </div>
  );
};

export default ContentWrapper;
